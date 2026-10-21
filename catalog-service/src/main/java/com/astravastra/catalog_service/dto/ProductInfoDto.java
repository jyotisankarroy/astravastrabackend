package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductInfoDto {
    private String description;
    private List<SpecificationDto> specification;
}
