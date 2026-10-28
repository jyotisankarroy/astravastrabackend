package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeOptionDto {
	private Long id;
    private String size;
    private Double price;
    private Boolean available;
    private String product_left;
}
