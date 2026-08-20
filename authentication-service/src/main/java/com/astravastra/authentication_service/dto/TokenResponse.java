package com.astravastra.authentication_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TokenResponse {
	
	private boolean status;
	
	private String message;
	
	private boolean existingUser;
	
	private String token;
	
	private String refreshToken;
	
}
