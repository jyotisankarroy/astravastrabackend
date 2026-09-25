package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcuctFilters {
	
	private List<GenderFilter> gender;
	
	private List<CategoryFilter> categories;
	
	private List<CategoryFilter> brands;
    
    private List<FilterOption> colors;
    
    private List<FilterOption> sizes;
    
    private Double minPrice;
    
    private Double maxPrice;
    
    private List<FilterOption> discountRanges;
}
