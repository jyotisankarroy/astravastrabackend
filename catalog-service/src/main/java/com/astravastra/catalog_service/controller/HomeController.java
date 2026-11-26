package com.astravastra.catalog_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.service.HomeService;
import com.astravastra.catalog_service.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	private final HomeService homeService;
	
	@GetMapping("/page1")
	public ResponseEntity<ResponseDto> getHome1PageData() {

		ResponseDto response = homeService.getHome1PageData();

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/page2")
	public ResponseEntity<ResponseDto> getHome2PageData(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		String userEmail = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userEmail = jwtUtil.extractEmail(token);
		}

		ResponseDto response = homeService.getHome2PageData(userEmail);

		return ResponseEntity.ok(response);
	}

}
