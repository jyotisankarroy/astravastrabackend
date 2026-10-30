package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductDetailsData {
	
    private List<BreadcrumbResponse> breadcrumb;
    
    private String brand;
    
    private String name;
    
    private String summary;
    
    private ProductInfoDto productDetails;
    
    private List<String> images;
    
    private RatingDto rating;
    
    private PriceDto price;
    
    private List<ColorDto> color;
    
    private SizeDataDto size;
    
    private String sellerName;
    
    private List<CustomerReviewDto> customerReview;
    
}
