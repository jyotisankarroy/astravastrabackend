package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceDto {
    private Double price;
    private Double offerPrice;
    private Integer discount;
}
