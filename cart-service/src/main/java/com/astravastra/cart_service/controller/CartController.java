package com.astravastra.cart_service.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astravastra.cart_service.dto.AddToCartRequest;
import com.astravastra.cart_service.dto.CartSelectionRequest;
import com.astravastra.cart_service.dto.RemoveCartRequest;
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

		ResponseDto response = cartService.addToCart(userEmail, request.getProductId(), request.getVariantId(),
				request.getQuantity());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// Fetch all items
	@PostMapping("/items/selection")
	public ResponseEntity<ResponseDto> getCart(@RequestHeader("X-Auth-User-Email") String userEmail,
			@RequestBody List<CartSelectionRequest> request) {

		ResponseDto response = cartService.updateCartSelection(userEmail, request);

		if (!response.isStatus()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		return ResponseEntity.ok(response);
	}

	// Remove items
	@DeleteMapping("/items/remove")
	public ResponseEntity<ResponseDto> removeCartItems(@RequestHeader("X-Auth-User-Email") String userEmail,
			@RequestBody List<RemoveCartRequest> request) {

		ResponseDto response = cartService.removeCartItems(userEmail, request);

		if (!response.isStatus()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		return ResponseEntity.ok(response);
	}

}
