package com.astravastra.catalog_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductFilterRequest {
	
    private List<Long> categoryIds;
    
    private List<String> brands;
    
    private String gender;
    
    private List<String> colors;
    
    private List<String> sizes;
    
    private Double minPrice;
    
    private Double maxPrice;
    
    private Integer discount;
    
    private String sort;
    
}
