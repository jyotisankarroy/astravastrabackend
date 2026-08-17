package com.astravastra.authentication_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astravastra.authentication_service.dto.RegisterRequest;
import com.astravastra.authentication_service.dto.TokenResponse;
import com.astravastra.authentication_service.dto.UserResponse;
import com.astravastra.authentication_service.entity.Otp;
import com.astravastra.authentication_service.entity.Users;
import com.astravastra.authentication_service.repository.OtpRepository;
import com.astravastra.authentication_service.repository.UserRepository;
import com.astravastra.authentication_service.security.JwtService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    
    public AuthService(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService,
			JwtService jwtService) {
		this.otpRepository = otpRepository;
		this.userRepository = userRepository;
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
		    
	    	UserResponse userResponse = new UserResponse();
	    	userResponse.setFirstName(user.getFirst_name());
	    	userResponse.setLastName(user.getLast_name());
	    	userResponse.setEmail(user.getEmail());
	    	userResponse.setGender(user.getGender());
	    	userResponse.setId(user.getId());
	    	userResponse.setMobile(user.getPhone());
	    	userResponse.setUuid(null);
	    	
	    	response.setUser(userResponse);
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
        user.setEmail(request.getEmail());
        user.setFirst_name(request.getFirstName());
        user.setLast_name(request.getLastName());
        user.setGender(request.getGender());
        user.setIs_active(true);
        user.setLocation(null);
        user.setPhone(request.getMobile());
        user.setStatus(0);
        user.setCreated_at(LocalDateTime.now());
        
        userRepository.save(user);

        // Generate Access & Refresh Tokens for immediate login
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        TokenResponse response = new TokenResponse();
        
        UserResponse userResponse = new UserResponse();
    	userResponse.setFirstName(user.getFirst_name());
    	userResponse.setLastName(user.getLast_name());
    	userResponse.setEmail(user.getEmail());
    	userResponse.setGender(user.getGender());
    	userResponse.setId(user.getId());
    	userResponse.setMobile(user.getPhone());
    	userResponse.setUuid(null);
    	
    	response.setUser(userResponse);
        response.setMessage("User registration successful");
        response.setStatus(true);
	    response.setExistingUser(false);
	    response.setToken(accessToken);
	    response.setRefreshToken(refreshToken);

        return response;
    }
}
