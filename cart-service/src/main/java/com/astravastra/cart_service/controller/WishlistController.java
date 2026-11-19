package com.astravastra.cart_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astravastra.cart_service.dto.ResponseDto;
import com.astravastra.cart_service.dto.WishlistRequest;
import com.astravastra.cart_service.service.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

	private final WishlistService wishlistService;

	@PostMapping("/add-items")
	public ResponseEntity<ResponseDto> addToWishlist(@RequestHeader("X-Auth-User-Email") String userEmail,
			@RequestBody WishlistRequest request) {

		ResponseDto response = wishlistService.addToWishlist(userEmail, request);

		return response.isStatus() ? ResponseEntity.status(HttpStatus.CREATED).body(response)
				: ResponseEntity.badRequest().body(response);

	}

	@GetMapping("/get-items")
	public ResponseEntity<ResponseDto> getWishlist(@RequestHeader("X-Auth-User-Email") String userEmail) {

		ResponseDto response = wishlistService.getWishlist(userEmail);

		return response.isStatus() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}

	@DeleteMapping("/remove/{productId}")
	public ResponseEntity<ResponseDto> removeFromWishlist(@RequestHeader("X-Auth-User-Email") String userEmail,
			@PathVariable Long productId) {

		ResponseDto response = wishlistService.removeFromWishlist(userEmail, productId);

		return response.isStatus() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}

}
