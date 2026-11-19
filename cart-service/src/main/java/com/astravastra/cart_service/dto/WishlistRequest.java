package com.astravastra.cart_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class WishlistRequest {
	
    private List<Long> productId;
    
}
