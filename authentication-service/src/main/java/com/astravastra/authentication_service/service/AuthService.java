package com.astravastra.authentication_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astravastra.authentication_service.dto.RegisterRequest;
import com.astravastra.authentication_service.dto.TokenResponse;
import com.astravastra.authentication_service.entity.Otp;
import com.astravastra.authentication_service.entity.RefreshToken;
import com.astravastra.authentication_service.entity.Users;
import com.astravastra.authentication_service.repository.OtpRepository;
import com.astravastra.authentication_service.repository.RefreshTokenRepository;
import com.astravastra.authentication_service.repository.UserRepository;
import com.astravastra.authentication_service.security.JwtService;

import io.jsonwebtoken.Claims;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    
    public AuthService(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService,
			JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
		this.otpRepository = otpRepository;
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.emailService = emailService;
		this.jwtService = jwtService;
	}

	@Transactional
    public boolean generateAndSendOtp(String email) {
        // Invalidate any existing unused OTPs for this email
        otpRepository.invalidatePreviousOtps(email);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Save to DB
        Otp otpRecord = new Otp();
        otpRecord.setEmail(email);
        otpRecord.setOtp_code(otp);
        otpRecord.setExpires_at(LocalDateTime.now().plusMinutes(5));
        otpRecord.setIs_used(false);
        otpRecord.setCreated_at(LocalDateTime.now());
        
        otpRepository.save(otpRecord);

        // Send Email
        boolean sendOtpEmail = emailService.sendOtpEmail(email, otp);
        
        return sendOtpEmail;
    }

	@Transactional
	public TokenResponse verifyOtpAndGetToken(String email, String otp) {

	    TokenResponse response = new TokenResponse();

	    // Check OTP exists
	    Otp otpRecord = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(email, otp);

	    if (otpRecord == null) {
	        response.setStatus(false);
	        response.setExistingUser(false);
	        response.setMessage("Invalid OTP");
	        return response;
	    }

	    // Check OTP expiry
	    if (otpRecord.getExpires_at().isBefore(LocalDateTime.now())) {
	        response.setStatus(false);
	        response.setExistingUser(false);
	        response.setMessage("OTP has expired");
	        return response;
	    }

	    // Mark OTP as used
	    otpRecord.setIs_used(true);
	    otpRepository.save(otpRecord);

	    // Check user exists
	    Users user = userRepository.findUserByEmail(email);

	    if (user != null) {
	    	// Generate token
	    	String accessToken = jwtService.generateAccessToken(user);
	        String refreshToken = jwtService.generateRefreshToken(user);
	        
	        // Save the initial Refresh Token
	        RefreshToken dbRefreshToken = new RefreshToken();
	        dbRefreshToken.setUser(user);
	        dbRefreshToken.setToken(refreshToken);
	        dbRefreshToken.setExpiryDate(jwtService.extractClaim(refreshToken, Claims::getExpiration));
	        
	        refreshTokenRepository.save(dbRefreshToken);
		    
	        response.setMessage("User already exists");
	        response.setStatus(true);
		    response.setExistingUser(true);
		    response.setToken(accessToken);
		    response.setRefreshToken(refreshToken);
	    } else {
	    	String registrationToken = jwtService.generateRegistrationToken(email);
	    	
	        response.setMessage("Otp verified successfully for New user");
	        response.setStatus(true);
		    response.setExistingUser(false);
		    response.setToken(registrationToken);
	    }

	    return response;
	}
	
	@Transactional
	public TokenResponse refreshAccessToken(String requestRefreshToken) {
	    String userEmail;
	    
	    // Verify the JWT signature and expiration
	    try {
	        userEmail = jwtService.extractUsername(requestRefreshToken);
	    } catch (Exception e) {
	        throw new RuntimeException("Refresh token is invalid or expired. Please log in again.");
	    }

	    Users user = userRepository.findUserByEmail(userEmail);
	    
	    if (user == null) {
			throw new RuntimeException("User not found");
		}
	    
	    // Check the Database
	    Optional<RefreshToken> dbTokenOpt = refreshTokenRepository.findByToken(requestRefreshToken);

	    // This means it was already used and rotated. We have a breach.
	    if (dbTokenOpt.isEmpty()) {
	        // Revoke ALL refresh tokens for this user.
	        refreshTokenRepository.deleteByUser(user);
	        
	        System.err.println("SECURITY ALERT: Token reuse detected for user " + userEmail + ". All sessions revoked.");
	        throw new RuntimeException("Security alert: Suspicious activity detected. Please log in again.");
	    }

	    // The token is valid and in the DB.
	    RefreshToken dbToken = dbTokenOpt.get();

	    // Delete the old token (Rotate)
	    refreshTokenRepository.delete(dbToken);

	    // Generate new tokens
	    String newAccessToken = jwtService.generateAccessToken(user);
	    String newRefreshToken = jwtService.generateRefreshToken(user);
	    
	    TokenResponse response = new TokenResponse();
	    
	    if (newAccessToken != null && !newAccessToken.isBlank()
	            && newRefreshToken != null && !newRefreshToken.isBlank()) {
	    	// Save the new refresh token to the DB
		    RefreshToken newDbToken = new RefreshToken();
		    newDbToken.setUser(user);
		    newDbToken.setToken(newRefreshToken);
		    newDbToken.setExpiryDate(jwtService.extractClaim(newRefreshToken, Claims::getExpiration));
		    
		    refreshTokenRepository.save(newDbToken);
		    
		    response.setMessage("Token refreshed successfully");
		    response.setToken(newRefreshToken);
		    response.setRefreshToken(newRefreshToken);
		    response.setStatus(true);
	    } else {
		    response.setMessage("Failed to generate access and refresh tokens");
		    response.setStatus(false);
	    }
	    
	    return new TokenResponse();
	}

    @Transactional
    public TokenResponse registerUser(RegisterRequest request, String registrationToken) {
        // Validate the registration token
        if (!jwtService.isValidRegistrationToken(registrationToken, request.getEmail())) {
            throw new RuntimeException("Invalid or expired registration token");
        }

        // Check if user already exists
        Users byEmail = userRepository.findUserByEmail(request.getEmail());
        
        if (byEmail != null) {
            throw new RuntimeException("Email already in use");
        }

        // Create and save new user
        Users user = new Users();
        user.setUuid(UUID.randomUUID().toString());
        user.setEmail(request.getEmail());
        user.setFirst_name(request.getFirstName());
        user.setLast_name(request.getLastName());
        user.setGender(request.getGender());
        user.setIs_active(true);
        user.setPhone(request.getMobile());
        user.setStatus(0);
        user.setCreated_at(LocalDateTime.now());
        
        userRepository.save(user);

        // Generate Access & Refresh Tokens for immediate login
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Save the initial Refresh Token
        RefreshToken dbRefreshToken = new RefreshToken();
        dbRefreshToken.setUser(user);
        dbRefreshToken.setToken(refreshToken);
        dbRefreshToken.setExpiryDate(jwtService.extractClaim(refreshToken, Claims::getExpiration));
        
        refreshTokenRepository.save(dbRefreshToken);
        
        TokenResponse response = new TokenResponse();
        
        response.setMessage("User registered successfully");
        response.setStatus(true);
	    response.setExistingUser(false);
	    response.setToken(accessToken);
	    response.setRefreshToken(refreshToken);

        return response;
    }
}
