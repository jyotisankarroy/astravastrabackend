package com.astravastra.catalog_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductFilterRequest {
	
    private List<Long> categoryIds; // e.g., [101, 102]
    
    private List<String> brands;       // e.g., ["Roadster", "Puma"]
    
    private String gender;             // e.g., "Men"
    
    private List<String> colors;       // e.g., ["Black", "Blue"]
    
    private List<String> sizes;        // e.g., ["M", "L", "XL"]
    
    private Double minPrice;           // e.g., 500.00
    
    private Double maxPrice;           // e.g., 2000.00
    
    private Integer minDiscount;       // e.g., 50 (for "50% and above")
    
    private String sort;
    
}
