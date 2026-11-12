package com.astravastra.cart_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {

    private Long cartItemId;
    private Long productId;

    private String brand;
    private String productName;

    private List<SizeDto> sizes;
    private String color;
    private String imageUrl;

    private Integer quantity;

    private Double price;
    private Double offerPrice;
    private Double discount;

    private Boolean selected;
    
}
