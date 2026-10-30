package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductInfoDto {
    private String brand;
    private String name;
    private String summary;
    private String description;
    private List<SpecificationDto> specification;
}
