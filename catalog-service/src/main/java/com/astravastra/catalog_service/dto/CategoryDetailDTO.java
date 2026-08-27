package com.astravastra.catalog_service.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CategoryDetailDTO {
    private Long id;
    
    private String name;
    
    private String slug;
    
    private Map<String, String> seo;
    
    private List<BreadcrumbDTO> breadcrumbs;
}
