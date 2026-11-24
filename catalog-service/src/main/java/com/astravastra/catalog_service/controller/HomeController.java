package com.astravastra.catalog_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.service.HomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
	
	private final HomeService homeService;
	
	@GetMapping("/page1")
	public ResponseEntity<ResponseDto> getHome1PageData() {

		ResponseDto response = homeService.getHome1PageData();

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/page2")
	public ResponseEntity<ResponseDto> getHome2PageData() {

		ResponseDto response = homeService.getHome2PageData();

		return ResponseEntity.ok(response);
	}

}
