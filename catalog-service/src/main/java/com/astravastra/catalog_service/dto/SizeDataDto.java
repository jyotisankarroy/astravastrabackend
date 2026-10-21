package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class SizeDataDto {
    private String sizeChart;
    private List<SizeOptionDto> sizeList;
}
