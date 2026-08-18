package com.astravastra.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astravastra.user_service.dto.RegisterRequest;
import com.astravastra.user_service.dto.ResponseDto;
import com.astravastra.user_service.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/profile")
	public ResponseEntity<ResponseDto> getUserProfile(@RequestHeader("X-Auth-User-Email") String userEmail) {

		// The Gateway guarantees that this header only exists if the JWT is valid.
		ResponseDto profileByEmail = userService.getProfileByEmail(userEmail);

		return ResponseEntity.ok(profileByEmail);
	}
	
	@PostMapping("/edit-profile")
	public ResponseEntity<ResponseDto> updateProfile(@RequestBody RegisterRequest request, @RequestHeader("X-Auth-User-Email") String userEmail) {

		ResponseDto updateProfile = userService.updateProfile(request, userEmail);

		return ResponseEntity.ok(updateProfile);
	}
	
	@PostMapping("/save-address")
	public ResponseEntity<ResponseDto> upsertAddress(@RequestBody RegisterRequest request) {

		ResponseDto upsertAddress = userService.upsertAddress(request);

		return ResponseEntity.ok(upsertAddress);
	}
	
	@GetMapping("/address-list")
	public ResponseEntity<ResponseDto> getAddresses(@RequestHeader("X-Auth-User-Email") String userEmail) {

		ResponseDto addresses = userService.getAddresses(userEmail);

		return ResponseEntity.ok(addresses);
	}
	
}
