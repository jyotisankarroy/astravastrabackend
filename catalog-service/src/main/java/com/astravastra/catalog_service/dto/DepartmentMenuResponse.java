package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentMenuResponse {

    private Long id;
    private String label;
    private String path;
    private Integer displayOrder;
    private List<CategoryMenuResponse> subCategory;

}
