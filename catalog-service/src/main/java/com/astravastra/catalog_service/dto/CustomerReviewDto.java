package com.astravastra.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReviewDto {
    private String customerName;
    private String date;
    private String review;
    private Long rating;
}
