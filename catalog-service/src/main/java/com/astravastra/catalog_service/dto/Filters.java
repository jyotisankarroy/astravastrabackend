package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Filters {
	
	private List<BrandFilterOption> categories;
	
	private List<BrandFilterOption> brands;
    
    private List<FilterOption> colors;
    
    private List<FilterOption> sizes;
    
    private Double minPrice;
    
    private Double maxPrice;
    
    private List<FilterOption> discountRanges;
}
