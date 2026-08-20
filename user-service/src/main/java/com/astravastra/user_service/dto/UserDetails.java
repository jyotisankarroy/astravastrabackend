package com.astravastra.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String gender;
	
	private String dob;
	
	private String phone;
	
	private String location;
	
	private Long pinCode;

}
