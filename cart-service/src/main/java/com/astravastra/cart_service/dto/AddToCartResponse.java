package com.astravastra.cart_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddToCartResponse {

    private Long cartItemId;
    private Long productId;
    private Long variantId;
    private String productName;
    private String size;
    private String color;
    private Integer quantity;
    private Double price;
    private Double offerPrice;
    private String message;
    
}
