package com.astravastra.catalog_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ShopByCategory {
	
	private List<CategoryDto> categoryList;
	
	private List<SubCategoryDto> subCategoryList;

}
