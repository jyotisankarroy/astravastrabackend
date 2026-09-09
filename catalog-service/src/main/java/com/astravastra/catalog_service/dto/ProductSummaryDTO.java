package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSummaryDTO {
	
    private Long productId;
    private String name;
    private Double startingPrice;
    private String images;
    private Long popularityScore;
    private Double customerRating;
    private Long discountPercentage;
    private String addedDate;
    private List<String> availableSizes;
    private String brand;
    
}
