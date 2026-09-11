package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSummaryDTO {
	
    private Long productId;
    private String name;
    private Double price;
    private Double offerPrice;
    private List<String> images;
    private Long popularityScore;
    private Double rating;
    private Long count;
    private Double discount;
    private String addedDate;
    private List<String> availableSizes;
    private String brand;
    
}
