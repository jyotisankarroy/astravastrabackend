package com.astravastra.cart_service.dto;

import lombok.Data;

@Data
public class CartSelectionRequest {

	private Long cartItemId;
	
	private Long variantId;

	private Boolean selected;

}
