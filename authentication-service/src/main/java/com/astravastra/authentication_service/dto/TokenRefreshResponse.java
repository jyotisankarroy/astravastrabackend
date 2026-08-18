package com.astravastra.authentication_service.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TokenRefreshResponse {
	
	private String accessToken;
	
    private String refreshToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public TokenRefreshResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
    
}
