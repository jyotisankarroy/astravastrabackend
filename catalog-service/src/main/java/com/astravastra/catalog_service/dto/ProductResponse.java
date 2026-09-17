package com.astravastra.catalog_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
	
    private Long productId;
    private String name;
    private Double price;
    private Double offerPrice;
    private List<String> images;
    private Long popularityScore;
    private Double rating;
    private Integer count;
    private Double discount;
    private LocalDateTime addedDate;
    private List<String> availableSizes;
    private String brand;
    
}
