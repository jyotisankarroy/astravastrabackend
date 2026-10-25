package com.astravastra.cart_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {

    private List<CartItemResponse> items;

    private Integer totalItems;
    private Integer selectedItems;

    private Double subtotal;
    
}
