package com.astravastra.user_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {
	
	private String firstName;
	
	private String middleName;

	private String lastName;

	private String email;

	private String phone;

	private String gender;
	
	private String dob;

	private String address;

	private long addressType;

	private String city;

	private long state;

	private String location;
	
	private String landmark;

	private long pinCode;
	
	private boolean isDefault;
	
	private Long addressId;
	
}
