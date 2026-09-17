package com.astravastra.catalog_service.service;

import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.CategoryDto;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.dto.ShopByCategory;
import com.astravastra.catalog_service.dto.SubCategoryDto;
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
		List<Category> allCategories = categoryRepository.findAllCtegoriesByDeptId(departmentId);

		ResponseDto response = new ResponseDto();
		ShopByCategory shopByCategory = new ShopByCategory();
		List<CategoryDto> categoryList = new ArrayList<>();
		List<SubCategoryDto> subCategoryList = new ArrayList<>();

		for (Category category : allCategories) {

			CategoryDto cat = new CategoryDto();

			cat.setId(category.getId());
			cat.setName(category.getName());
			cat.setPath(category.getSlug());
			cat.setImage(category.getImage());

			categoryList.add(cat);
			
			List<Category> allSubCategories = categoryRepository.findAllSubCtegoriesByParentId(category.getId());

			for (Category subCategory : allSubCategories) {

				SubCategoryDto sCat = new SubCategoryDto();

				sCat.setId(subCategory.getId());
				sCat.setName(subCategory.getName());
				sCat.setPath(subCategory.getSlug());
				sCat.setImage(subCategory.getImage());

				subCategoryList.add(sCat);
			}

		}

		shopByCategory.setCategoryList(categoryList);
		shopByCategory.setSubCategoryList(subCategoryList);

		response.setStatus(true);
		response.setData(shopByCategory);
		return response;
	}

}
