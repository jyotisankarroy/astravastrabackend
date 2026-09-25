package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenderFilter {
	
	private Integer id;
    
    private String name;
    
    private boolean checked;

}
