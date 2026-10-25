package com.astravastra.cart_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astravastra.cart_service.dto.AddToCartRequest;
import com.astravastra.cart_service.dto.ResponseDto;
import com.astravastra.cart_service.service.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	// Add items
	@PostMapping("/add-items")
	public ResponseEntity<ResponseDto> addToCart(@RequestHeader("X-Auth-User-Email") String userEmail,
			@RequestBody AddToCartRequest request) {

		ResponseDto response = cartService.addToCart(userEmail, request.getVariantId(), request.getQuantity());

		if (!response.isStatus()) {
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// Fetch all items
	@GetMapping("/items")
	public ResponseEntity<ResponseDto> getCart(@RequestHeader("X-Auth-User-Email") String userEmail) {

		ResponseDto response = cartService.getCart(userEmail);

		if (!response.isStatus()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		return ResponseEntity.ok(response);
	}

}
