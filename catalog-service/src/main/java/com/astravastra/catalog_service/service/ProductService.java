package com.astravastra.catalog_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astravastra.catalog_service.dto.BrandFilterOption;
import com.astravastra.catalog_service.dto.FilterOption;
import com.astravastra.catalog_service.dto.Filters;
import com.astravastra.catalog_service.dto.Metadata;
import com.astravastra.catalog_service.dto.ProductFilterRequest;
import com.astravastra.catalog_service.dto.ProductGridResponseDTO;
import com.astravastra.catalog_service.dto.ProductSummaryDTO;
import com.astravastra.catalog_service.entity.Brand;
import com.astravastra.catalog_service.entity.Category;
import com.astravastra.catalog_service.entity.Product;
import com.astravastra.catalog_service.entity.ProductImage;
import com.astravastra.catalog_service.entity.ProductVariant;
import com.astravastra.catalog_service.repository.CategoryRepository;
import com.astravastra.catalog_service.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional(readOnly = true)
	public ProductGridResponseDTO getProductsByCategory(ProductFilterRequest filterRequest, Long categoryId, int page, int size) {

		// Fetch all products for the category
		List<Product> productPage = productRepository.findByCategoryId(categoryId, size, page);

		Long totalProductCount = productRepository.totalCountsByCategoryId(categoryId);

		// Fetch the target category
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found : " + categoryId));

		List<ProductSummaryDTO> productDTOs = new ArrayList<>();

		// Map Entities to DTOs
		for (Product product : productPage) {

			ProductSummaryDTO dto = mapToProductSummaryDTO(product);

			productDTOs.add(dto);
		}

		// Apply the Sorting Logic
		sortProducts(productDTOs, filterRequest.getSort());

		// Build breadcrumb Trail
		List<String> breadcrumbLabels = new ArrayList<>();
		Category current = category;

		// Climb the category tree
		while (current != null) {

			breadcrumbLabels.add(current.getName());

			current = current.getParentId();
		}

		// Reverse: Clothing -> Men Topwear
		Collections.reverse(breadcrumbLabels);

		// Add Home at beginning
		breadcrumbLabels.add(0, "Home");

		String breadcrumb = String.join(" / ", breadcrumbLabels);
		
		// Fetch Dynamic Filters
//		List<Map<String, Object>> categories = productRepository.findCategoriesByCategoryId(categoryId);
		List<Brand> rawBrands = productRepository.findDistinctBrandsByCategoryId(categoryId);
	    List<String> colors = productRepository.findDistinctColorsByCategoryId(categoryId);
	    List<String> sizes = productRepository.findDistinctSizesByCategoryId(categoryId);
	    Double minPrice = productRepository.findMinPriceByCategoryId(categoryId);
	    Double maxPrice = productRepository.findMaxPriceByCategoryId(categoryId);
	    
//	    List<BrandFilterOption> categoryList = new ArrayList<>();
//	    for (Map<String, Object> cat : categories) {
//	    	BrandFilterOption filterOption = new BrandFilterOption();
//	    	filterOption.setId(Integer.parseInt(cat.get("id").toString()));
//	    	filterOption.setName(cat.get("label").toString());
//	    	filterOption.setChecked(false);
//	    	categoryList.add(filterOption);
//		}
	    
	    List<FilterOption> colourList = new ArrayList<>();
	    for (String string : colors) {
	    	FilterOption filterOption = new FilterOption();
	    	filterOption.setName(string);
	    	filterOption.setChecked(false);
	    	colourList.add(filterOption);
		}
	    
	    List<FilterOption> sizeList = new ArrayList<>();
	    for (String string : sizes) {
	    	FilterOption filterOption = new FilterOption();
	    	filterOption.setName(string);
	    	filterOption.setChecked(false);
	    	sizeList.add(filterOption);
		}
	    
	    // Static standard discount ranges
	    List<String> discountRanges = Arrays.asList(
	            "10% and above", "20% and above", "30% and above",
	            "40% and above", "50% and above", "60% and above"
	    );
	    
	    List<FilterOption> discountRangsList = new ArrayList<>();
	    for (String string : discountRanges) {
	    	FilterOption filterOption = new FilterOption();
	    	filterOption.setName(string);
	    	filterOption.setChecked(false);
	    	discountRangsList.add(filterOption);
		}
	    
	    List<BrandFilterOption> brandFilters = rawBrands.stream()
	    	    .map(b -> BrandFilterOption.builder()
	    	            .id(b.getId())
	    	            .name(b.getName())
	    	            .build())
	    	    .collect(Collectors.toList());
	    
	    Filters filters = Filters.builder()
	    		.categories(new ArrayList<>())
	            .brands(brandFilters)
	            .colors(colourList)
	            .sizes(sizeList)
	            .minPrice(minPrice != null ? minPrice : 0.0)
	            .maxPrice(maxPrice != null ? maxPrice : 0.0)
	            .discountRanges(discountRangsList)
	            .build();

		// Build and return the final response
		return ProductGridResponseDTO.builder().status(true)
				.metadata(Metadata.builder().breadcrumb(breadcrumb).totalItems(totalProductCount).build())
				.filters(filters)
				.data(productDTOs).build();
	}

	private ProductSummaryDTO mapToProductSummaryDTO(Product product) {
		
		Double price = 0.0;

		if (product.getVariants() != null) {

			for (ProductVariant variant : product.getVariants()) {

				if (variant.getPrice() != null) {
					
					price = variant.getPrice();

				}
			}
		}
		
		Integer discountPercentage = product.getDiscountPercentage();
		
		double discount = (discountPercentage != null) ? discountPercentage.doubleValue() : 0.0;
		
		double discountedPrice = price * (1.0 - (discount / 100.0));
		
		

		List<String> availableSizes = new ArrayList<>();

		if (product.getVariants() != null) {

			for (ProductVariant variant : product.getVariants()) {

				if (variant.getStockQuantity() > 0 && variant.getSize() != null
						&& !availableSizes.contains(variant.getSize())) {

					availableSizes.add(variant.getSize());
				}
			}
		}
		
		List<String> images = new ArrayList<>();

		if (product.getImages() != null && !product.getImages().isEmpty()) {

			for (ProductImage image : product.getImages()) {

				images.add(image.getImageUrl());

			}

		}

		return ProductSummaryDTO.builder().productId(product.getId()).name(product.getName()).count(0l).discount(discount)
				.price(price).offerPrice(discountedPrice).availableSizes(availableSizes).images(images).brand(product.getBrand().getName()).build();
	}

	private List<ProductSummaryDTO> sortProducts(List<ProductSummaryDTO> list, String sortParam) {
		if (sortParam == null) {
			return list; // Default "Recommended"
		}

		switch (sortParam.toLowerCase()) {
		case "popularity":
			list.sort(Comparator.comparing(ProductSummaryDTO::getPopularityScore).reversed());
			break;
		case "whats_new":
			list.sort(Comparator
					.comparing(ProductSummaryDTO::getAddedDate, Comparator.nullsLast(Comparator.naturalOrder()))
					.reversed());
			break;
		case "discount": // Better Discount
			list.sort(Comparator.comparing(ProductSummaryDTO::getDiscount).reversed());
			break;
		case "price_desc": // Price: High to Low
			list.sort(Comparator.comparing(ProductSummaryDTO::getPrice).reversed());
			break;
		case "price_asc": // Price: Low to High
			list.sort(Comparator.comparing(ProductSummaryDTO::getPrice));
			break;
		case "rating": // Customer Rating
			list.sort(Comparator.comparing(ProductSummaryDTO::getRating).reversed());
			break;
		default:
			// "recommended" or unrecognized fallback
			break;
		}
		return list;
	}

}
