package com.astravastra.catalog_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astravastra.catalog_service.dto.ProductFilterRequest;
import com.astravastra.catalog_service.dto.ProductListingResponse;
import com.astravastra.catalog_service.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/filter/categoryid/{categoryId}/limit/{limit}/offset/{offset}")
    public ResponseEntity<ProductListingResponse> getProducts(
    		@PathVariable Long categoryId,
    		@PathVariable int limit,
    		@PathVariable int offset,
    		@RequestBody ProductFilterRequest filterRequest) {

    	ProductListingResponse response = productService.getProductsByCategory(filterRequest, categoryId, offset, limit);
        
        return ResponseEntity.ok(response);
    }
    
}
