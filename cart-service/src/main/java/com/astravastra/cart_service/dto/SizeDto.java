package com.astravastra.cart_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SizeDto {

    private Long variantId;
    private String size;
    private Integer availableStock;
    private Boolean selectedSize;
    private Boolean inStock;
    
}
