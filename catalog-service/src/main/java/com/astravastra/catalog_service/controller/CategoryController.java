package com.astravastra.catalog_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.astravastra.catalog_service.dto.CategoryDetailDTO;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping("/categoryId/{categoryId}")
    public ResponseEntity<ResponseDto> shopByCategory(@PathVariable("categoryId") long categoryId) {
    	
        ResponseDto response = categoryService.shopByCategory(categoryId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("{slug}")
    public ResponseEntity<CategoryDetailDTO> getCategoryBySlug(@PathVariable("slug") String slug) {
    	
        CategoryDetailDTO response = categoryService.getCategoryDetails(slug);
        
        return ResponseEntity.ok(response);
    }
    
}
