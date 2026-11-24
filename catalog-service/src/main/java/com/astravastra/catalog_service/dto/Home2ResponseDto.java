package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Home2ResponseDto {

    private List<ProductResponse> trendingProducts;
    private List<ProductResponse> discountedProducts;

}
