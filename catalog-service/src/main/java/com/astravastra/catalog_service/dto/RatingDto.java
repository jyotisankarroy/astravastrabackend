package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class RatingDto {
    private Double averageRating;
    private Long totalCount;
    private List<RatingDivisionDto> ratingDivision;
}
