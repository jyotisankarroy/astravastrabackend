package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorDto {
	private Long id;
    private String name;
    private Boolean checked;
    private String image;
}
