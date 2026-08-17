package com.astravastra.authentication_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astravastra.authentication_service.dto.RegisterRequest;
import com.astravastra.authentication_service.dto.TokenResponse;
import com.astravastra.authentication_service.service.AuthService;

@RestController
@RequestMapping("/api/user")
public class RegistrationController {
	
	private final AuthService authService;
	
	public RegistrationController(AuthService authService) {
		this.authService = authService;
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
