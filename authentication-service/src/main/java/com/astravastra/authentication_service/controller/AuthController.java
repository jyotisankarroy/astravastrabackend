package com.astravastra.authentication_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astravastra.authentication_service.dto.EmailRequest;
import com.astravastra.authentication_service.dto.Response;
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

}
