package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetadata {
	
    private long totalItems;
    private List<BreadcrumbResponse> breadcrumb;    
    
}
