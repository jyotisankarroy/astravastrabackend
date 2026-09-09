package com.astravastra.catalog_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Metadata {
	
    private long totalItems;
    private String breadcrumb;    
    
}
