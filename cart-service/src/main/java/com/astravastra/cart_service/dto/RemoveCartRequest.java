package com.astravastra.cart_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class RemoveCartRequest {

	private List<Long> cartItemId;
	
}
