package com.astravastra.catalog_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astravastra.catalog_service.dto.ProductFilterRequest;
import com.astravastra.catalog_service.dto.ProductListingResponse;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.service.ProductService;
import com.astravastra.catalog_service.util.JwtUtil;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private JwtUtil jwtUtil;

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/filter/categoryid/{categoryId}/limit/{limit}/offset/{offset}")
	public ResponseEntity<ProductListingResponse> getProducts(@PathVariable Long categoryId, @PathVariable int limit,
			@PathVariable int offset, @RequestBody ProductFilterRequest filterRequest,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		String userEmail = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userEmail = jwtUtil.extractEmail(token);
		}

		ProductListingResponse response = productService.getProductsByCategory(filterRequest, categoryId, offset, limit,
				userEmail);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{productId}/details")
	public ResponseEntity<ResponseDto> getProductDetails(@PathVariable Long productId,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		String userEmail = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userEmail = jwtUtil.extractEmail(token);
		}

		ResponseDto response = productService.getProductDetails(productId, userEmail);

		return ResponseEntity.ok(response);
	}

}
