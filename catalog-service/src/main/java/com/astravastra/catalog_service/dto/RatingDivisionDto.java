package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDivisionDto {
    private Integer rating;
    private Long count;
    private Integer percentage;
}
