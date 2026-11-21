package com.astravastra.catalog_service.service;

import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.BrandDto;
import com.astravastra.catalog_service.dto.CategoryDto;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.dto.ShopByCategory;
import com.astravastra.catalog_service.dto.SubCategoryDto;
import com.astravastra.catalog_service.entity.Brand;
import com.astravastra.catalog_service.entity.Category;
import com.astravastra.catalog_service.repository.CategoryRepository;

import java.util.*;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

	public ResponseDto shopByCategory(long departmentId) {
		List<Category> allCategories = categoryRepository.findAllCategoriesByDeptId(departmentId);

		ResponseDto response = new ResponseDto();
		ShopByCategory shopByCategory = new ShopByCategory();
		List<CategoryDto> categoryList = new ArrayList<>();
		List<SubCategoryDto> subCategoryList = new ArrayList<>();
		List<BrandDto> brandList = new ArrayList<>();
		Set<Long> addedBrandIds = new HashSet<>();

		for (Category category : allCategories) {

			CategoryDto cat = new CategoryDto();

			cat.setId(category.getId());
			cat.setName(category.getName());
			cat.setPath(category.getSlug());
			cat.setImage(category.getImage());

			categoryList.add(cat);
			
			List<Category> allSubCategories = categoryRepository.findAllSubCategoriesByParentId(category.getId());

			for (Category subCategory : allSubCategories) {

				SubCategoryDto sCat = new SubCategoryDto();

				sCat.setId(subCategory.getId());
				sCat.setName(subCategory.getName());
				sCat.setPath(subCategory.getSlug());
				sCat.setImage(subCategory.getImage());

				subCategoryList.add(sCat);
				
				List<Brand> allBrands = categoryRepository.findAllBrandsBySubCategoryId(subCategory.getId());
				
				for (Brand brand : allBrands) {
					
					if (addedBrandIds.add(brand.getId())) {
				        BrandDto brandDto = new BrandDto();
				        brandDto.setId(brand.getId());
				        brandDto.setImage(brand.getLogoUrl());
				        brandDto.setName(brand.getName());
				        brandDto.setPath("clothing");
				        
				        brandList.add(brandDto);
				    }
					
				}
				
			}

		}

		shopByCategory.setCategoryList(categoryList);
		shopByCategory.setSubCategoryList(subCategoryList);
		shopByCategory.setBrands(brandList);

		response.setStatus(true);
		response.setData(shopByCategory);
		return response;
	}

}
