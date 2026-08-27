package com.astravastra.catalog_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astravastra.catalog_service.dto.BreadcrumbDTO;
import com.astravastra.catalog_service.dto.CategoryDetailDTO;
import com.astravastra.catalog_service.dto.CategoryDto;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.dto.ShopByCategory;
import com.astravastra.catalog_service.dto.SubCategoryDto;
import com.astravastra.catalog_service.entity.Category;
import com.astravastra.catalog_service.entity.NavigationMenu;
import com.astravastra.catalog_service.repository.CategoryRepository;
import com.astravastra.catalog_service.repository.NavigationMenuRepository;

import java.util.*;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final NavigationMenuRepository menuRepository;

	public CategoryService(CategoryRepository categoryRepository, NavigationMenuRepository menuRepository) {
		this.categoryRepository = categoryRepository;
		this.menuRepository = menuRepository;
	}

	@Transactional(readOnly = true)
	public CategoryDetailDTO getCategoryDetails(String slug) {

		// Fetch the target category
		Category category = categoryRepository.findBySlug(slug)
				.orElseThrow(() -> new RuntimeException("Category not found for slug: " + slug));

		// Build the Breadcrumb Trail
		List<BreadcrumbDTO> breadcrumbs = new ArrayList<>();
		Category current = category;

		// Climb the tree until we hit a root category (parent is null)
		while (current != null) {
			breadcrumbs.add(new BreadcrumbDTO(current.getName(), "/category/" + current.getSlug()));
			current = current.getParentId(); // This triggers a fast Primary Key lookup in JPA
		}

		// Reverse the list so it reads Top-Down (e.g., Clothing -> Men Topwear)
		Collections.reverse(breadcrumbs);

		breadcrumbs.add(0, new BreadcrumbDTO("Home", "/"));

		Map<String, String> seoMetadata = new HashMap<>();
		seoMetadata.put("title",
				category.getSeoTitle() != null ? category.getSeoTitle() : category.getName() + " - Buy Online");
		seoMetadata.put("description", category.getSeoDescription() != null ? category.getSeoDescription()
				: "Shop the latest " + category.getName());

		CategoryDetailDTO responseDTO = new CategoryDetailDTO();
		responseDTO.setId(category.getId());
		responseDTO.setName(category.getName());
		responseDTO.setSlug(category.getSlug());
		responseDTO.setSeo(seoMetadata);
		responseDTO.setBreadcrumbs(breadcrumbs);

		return responseDTO;
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
