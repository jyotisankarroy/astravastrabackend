package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Home1ResponseDto {

    private String latestProductImage;
    private List<BannerDto> banners;
    private List<BrandDto> topBrands;
    private List<BrandDto> topCategories;

}
