package com.astravastra.authentication_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String mobile;
	
	private String gender;

}
