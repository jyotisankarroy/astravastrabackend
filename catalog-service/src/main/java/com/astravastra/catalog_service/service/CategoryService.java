package com.astravastra.catalog_service.service;

import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.CategoryDto;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.dto.ShopByCategory;
import com.astravastra.catalog_service.dto.SubCategoryDto;
import com.astravastra.catalog_service.entity.NavigationMenu;
import com.astravastra.catalog_service.repository.NavigationMenuRepository;

import java.util.*;

@Service
public class CategoryService {

	private final NavigationMenuRepository menuRepository;

	public CategoryService(NavigationMenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	public ResponseDto shopByCategory(long categoryId) {
		List<NavigationMenu> byParentId = menuRepository.findByParentId(categoryId);

		ResponseDto response = new ResponseDto();
		ShopByCategory shopByCategory = new ShopByCategory();
		List<CategoryDto> categoryList = new ArrayList<>();
		List<SubCategoryDto> subCategoryList = new ArrayList<>();

		for (NavigationMenu category : byParentId) {

			CategoryDto cat = new CategoryDto();

			cat.setId(category.getId());
			cat.setName(category.getLabel());
			cat.setPath(category.getSlug());
			cat.setImage(category.getImage());

			categoryList.add(cat);

			List<NavigationMenu> byParentId2 = menuRepository.findByParentId(category.getId());

			for (NavigationMenu subCategory : byParentId2) {

				SubCategoryDto sCat = new SubCategoryDto();

				sCat.setId(subCategory.getId());
				sCat.setName(subCategory.getLabel());
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
