package com.astravastra.cart_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {

    private Long cartItemId;
    private Long productId;
    private Long variantId;

    private String brand;
    private String productName;
    private String slug;

    private String size;
    private String color;
    private String imageUrl;

    private Integer quantity;
    private Integer availableStock;

    private Double price;
    private Double offerPrice;
    private Double discountPercentage;

    private Boolean selected;
    private Boolean inStock;
    
}
