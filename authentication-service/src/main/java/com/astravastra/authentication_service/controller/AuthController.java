package com.astravastra.authentication_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astravastra.authentication_service.dto.EmailRequest;
import com.astravastra.authentication_service.dto.RegisterRequest;
import com.astravastra.authentication_service.dto.Response;
import com.astravastra.authentication_service.dto.TokenRefreshResponse;
import com.astravastra.authentication_service.dto.TokenResponse;
import com.astravastra.authentication_service.dto.VerifyOtpRequest;
import com.astravastra.authentication_service.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/send-otp")
	public ResponseEntity<Response> sendOtp(@RequestBody EmailRequest request) {
		Response response = new Response();

		boolean andSendOtp = authService.generateAndSendOtp(request.getEmail());

		if (andSendOtp) {
			response.setMessage("OTP sent successfully");
			response.setStatus(true);
		} else {
			response.setMessage("Failed to send OTP");
			response.setStatus(false);
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<TokenResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {

		TokenResponse verifyOtpAndGetToken = authService.verifyOtpAndGetToken(request.getEmail(), request.getOtp());

		return ResponseEntity.ok(verifyOtpAndGetToken);
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<Response> resendOtp(@RequestBody EmailRequest request) {
		Response response = new Response();

		boolean andSendOtp = authService.generateAndSendOtp(request.getEmail());

		if (andSendOtp) {
			response.setMessage("A new OTP has been sent successfully");
			response.setStatus(true);
		} else {
			response.setMessage("Failed to resend OTP");
			response.setStatus(false);
		}
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<TokenRefreshResponse> refreshTokens(@RequestHeader("Authorization") String authHeader) {
		
		String refreshToken = authHeader.substring(7);
		
		TokenRefreshResponse response = authService.refreshAccessToken(refreshToken);
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/register")
	public ResponseEntity<TokenResponse> register(@RequestHeader("Authorization") String authHeader,
			@RequestBody RegisterRequest request) {

		// Extract token by removing "Bearer " prefix
		String registrationToken = authHeader.substring(7);

		TokenResponse response = authService.registerUser(request, registrationToken);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
