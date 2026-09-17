package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMenuResponse {
    private Long id;
    private String label;
    private String path;
    private Integer displayOrder;
    
    private List<CategoryMenuResponse> subCategory;

}
