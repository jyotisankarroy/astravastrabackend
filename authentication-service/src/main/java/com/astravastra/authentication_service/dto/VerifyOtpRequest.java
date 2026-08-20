package com.astravastra.authentication_service.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
	
	private String otp;
	
	private String email;

}
