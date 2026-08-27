package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BreadcrumbDTO {	
    private String label;
    
    private String path;
}
