package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductDetailsData {
	
    private List<BreadcrumbResponse> breadcrumb;
    
    private String title;
    
    private String summary;
    
    private List<String> images;
    
    private RatingDto rating;
    
    private PriceDto price;
    
    private List<ColorDto> color;
    
    private SizeDataDto size;
    
    private String sellerName;
    
    private ProductInfoDto productDetails;
    
    private List<CustomerReviewDto> customerReview;
    
}
