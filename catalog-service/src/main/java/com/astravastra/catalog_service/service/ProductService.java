package com.astravastra.catalog_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.astravastra.catalog_service.dto.BreadcrumbResponse;
import com.astravastra.catalog_service.dto.CategoryFilter;
import com.astravastra.catalog_service.dto.ColorDto;
import com.astravastra.catalog_service.dto.CustomerReviewDto;
import com.astravastra.catalog_service.dto.FilterOption;
import com.astravastra.catalog_service.dto.GenderFilter;
import com.astravastra.catalog_service.dto.PriceDto;
import com.astravastra.catalog_service.dto.PriceFilter;
import com.astravastra.catalog_service.dto.ProcuctFilters;
import com.astravastra.catalog_service.dto.ProductDetailsData;
import com.astravastra.catalog_service.dto.ProductFilterRequest;
import com.astravastra.catalog_service.dto.ProductInfoDto;
import com.astravastra.catalog_service.dto.ProductListingResponse;
import com.astravastra.catalog_service.dto.ProductMetadata;
import com.astravastra.catalog_service.dto.ProductResponse;
import com.astravastra.catalog_service.dto.RatingDivisionDto;
import com.astravastra.catalog_service.dto.RatingDto;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.dto.SizeDataDto;
import com.astravastra.catalog_service.dto.SizeOptionDto;
import com.astravastra.catalog_service.dto.SpecificationDto;
import com.astravastra.catalog_service.entity.Brand;
import com.astravastra.catalog_service.entity.Category;
import com.astravastra.catalog_service.entity.Gender;
import com.astravastra.catalog_service.entity.Product;
import com.astravastra.catalog_service.entity.ProductAttribute;
import com.astravastra.catalog_service.entity.ProductImage;
import com.astravastra.catalog_service.entity.ProductReview;
import com.astravastra.catalog_service.entity.ProductSeller;
import com.astravastra.catalog_service.entity.ProductVariant;
import com.astravastra.catalog_service.repository.CategoryRepository;
import com.astravastra.catalog_service.repository.GenderRepository;
import com.astravastra.catalog_service.repository.ProductAttributeRepository;
import com.astravastra.catalog_service.repository.ProductImageRepository;
import com.astravastra.catalog_service.repository.ProductRepository;
import com.astravastra.catalog_service.repository.ProductReviewRepository;
import com.astravastra.catalog_service.repository.ProductSellerRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final GenderRepository genderRepository;
	private final ProductImageRepository productImageRepository;
	private final ProductAttributeRepository productAttributeRepository;
	private final ProductReviewRepository productReviewRepository;
	private final ProductSellerRepository productSellerRepository;

	@Transactional(readOnly = true)
	public ProductListingResponse getProductsByCategory(ProductFilterRequest filterRequest, Long categoryId, int offset,
			int limit) {

		ProductListingResponse response = new ProductListingResponse();

		// category hierarchy
		List<Long> categoryIds = getCategoryIds(categoryId);

		// if UI has selected category filters, use those selected categories instead.
		if (filterRequest.getCategoryIds() != null && !filterRequest.getCategoryIds().isEmpty()) {
			categoryIds = filterRequest.getCategoryIds();
		}

		// normalize filters
		List<String> brands = nullIfEmpty(filterRequest.getBrands());
		List<String> colors = nullIfEmpty(filterRequest.getColors());
		List<String> sizes = nullIfEmpty(filterRequest.getSizes());

		boolean brandsEnabled = brands != null && !brands.isEmpty();
		boolean colorsEnabled = colors != null && !colors.isEmpty();
		boolean sizesEnabled = sizes != null && !sizes.isEmpty();

		// sorting
		String sort = filterRequest.getSort();

		if (sort == null || sort.isBlank()) {
			sort = "whats_new";
		}

		// get filtered products
		List<Product> products = productRepository.findFilteredProducts(categoryIds, brands, brandsEnabled,
				filterRequest.getGender(), colors, colorsEnabled, sizes, sizesEnabled, filterRequest.getMinPrice(),
				filterRequest.getMaxPrice(), filterRequest.getDiscount(), sort, offset, limit);

		// total count
		long totalItems = productRepository.countProductsByCategoryIds(categoryIds);

		// metadata
		ProductMetadata metadata = new ProductMetadata();
		metadata.setTotalItems(totalItems);
		metadata.setBreadcrumb(buildBreadcrumb(categoryId));
		response.setMetadata(metadata);

		// filters
		ProcuctFilters filters = new ProcuctFilters();

		filters.setGender(getGenderFilters(categoryId, filterRequest));

		filters.setCategories(getCategoryFilters(categoryId, filterRequest));

		filters.setBrands(getBrandFilters(categoryIds, filterRequest));

		filters.setColors(getColorFilters(categoryIds, filterRequest));

		filters.setSizes(getSizeFilters(categoryIds, filterRequest));

		PriceFilter priceFilters = getPriceFilters(categoryIds, filterRequest);
		filters.setMinPrice(priceFilters.getMinPrice());

		filters.setMaxPrice(priceFilters.getMaxPrice());

		filters.setDiscountRanges(getDiscountRanges(filterRequest));

		response.setFilters(filters);

		// product response
		response.setData(products.stream().map(product -> convertToProductResponse(product, filterRequest.getSizes(),
				filterRequest.getMinPrice(), filterRequest.getMaxPrice())).toList());

		response.setStatus(true);

		return response;

	}

	private List<Long> getCategoryIds(Long categoryId) {

		List<Category> subCategories = null;

		List<Long> mainCategoryIds = categoryRepository.findMainCategories();

		if (mainCategoryIds.contains(categoryId)) {
			subCategories = categoryRepository.findFinalSubCtegoriesByParentId(categoryId);
		} else {
			subCategories = categoryRepository.findAllSubCtegoriesByParentId(categoryId);
		}

		if (subCategories.isEmpty()) {
			return List.of(categoryId);
		}

		return subCategories.stream().map(Category::getId).toList();
	}

	private List<GenderFilter> getGenderFilters(Long categoryId, ProductFilterRequest filterRequest) {

		List<Long> mainCategoryIds;

		if (categoryId == 224 || categoryId == 230 || categoryId == 233 || categoryId == 236) {
			mainCategoryIds = new ArrayList<>();
			mainCategoryIds.add(categoryId);
		} else {
			mainCategoryIds = categoryRepository.findMainCategories();
		}

		String gender = filterRequest.getGender();

		List<GenderFilter> filters = new ArrayList<>();

		if (mainCategoryIds.contains(categoryId)) {
			List<Gender> allGender = genderRepository.findAllGender();

			if (categoryId == 224 || categoryId == 230 || categoryId == 233 || categoryId == 236) {
				allGender.remove(0);
				allGender.remove(0);
			}

			for (Gender g : allGender) {
				GenderFilter filter = new GenderFilter();
				filter.setId(g.getId());
				filter.setName(g.getName());
				filter.setChecked(gender != null && gender.contains(g.getName()));
				filters.add(filter);
			}

		} else {
			return null;
		}

		return filters;

	}

	private List<CategoryFilter> getCategoryFilters(Long categoryId, ProductFilterRequest request) {

		List<Category> categories = null;

		List<Long> mainCategoryIds = categoryRepository.findMainCategories();

		if (mainCategoryIds.contains(categoryId)) {

			if (request.getGender() != null) {

				if (request.getGender().equalsIgnoreCase("Men")) {
					categories = categoryRepository.findAllCategoryByGender(1l, categoryId);
				} else if (request.getGender().equalsIgnoreCase("Women")) {
					categories = categoryRepository.findAllCategoryByGender(2l, categoryId);
				} else if (request.getGender().equalsIgnoreCase("Boys")) {
					categories = categoryRepository.findAllCategoryByGender(3l, categoryId);
				} else {
					categories = categoryRepository.findAllCategoryByGender(4l, categoryId);
				}
			} else { // Example -> Clothing, Accesorries
				categories = categoryRepository.findFinalSubCtegoriesByParentId(categoryId);
			}

		} else {
			categories = categoryRepository.findAllSubCtegoriesByParentId(categoryId);
		}

		List<Long> selectdCategoryIds = request.getCategoryIds();

		List<CategoryFilter> filters = new ArrayList<>();

		for (Category category : categories) {
			Long countProductsByCategoryId = productRepository.countProductsByCategoryId(category.getId());

			CategoryFilter filter = new CategoryFilter();

			filter.setId(category.getId().intValue());
			filter.setName(category.getMenuName());
			filter.setChecked(selectdCategoryIds != null && selectdCategoryIds.contains(category.getId()));
			filter.setCount(countProductsByCategoryId);
			filters.add(filter);
		}

		return filters;
	}

	private List<CategoryFilter> getBrandFilters(List<Long> categoryIds, ProductFilterRequest request) {

		List<Brand> brands = productRepository.findBrandsByCategoryIds(categoryIds);

		List<String> selectedBrands = request.getBrands();

		List<CategoryFilter> filters = new ArrayList<>();

		for (Brand brand : brands) {
			Long countProductsByBrandId = productRepository.countProductsByBrandId(brand.getId());

			CategoryFilter filter = new CategoryFilter();

			filter.setId(brand.getId());
			filter.setName(brand.getName());
			filter.setChecked(selectedBrands != null && selectedBrands.contains(brand.getName()));
			filter.setCount(countProductsByBrandId);
			filters.add(filter);
		}

		return filters;
	}

	private List<FilterOption> getColorFilters(List<Long> categoryIds, ProductFilterRequest request) {

		List<String> colors = productRepository.findColorsByCategoryIds(categoryIds);

		List<String> selectedColors = request.getColors();

		List<FilterOption> filters = new ArrayList<>();

		for (String color : colors) {
			Long countProductsByColor = productRepository.countProductsByColor(color);

			FilterOption filter = new FilterOption();

			filter.setName(color);
			filter.setChecked(selectedColors != null && selectedColors.contains(color));
			filter.setCount(countProductsByColor);
			filters.add(filter);
		}

		return filters;
	}

	private List<FilterOption> getSizeFilters(List<Long> categoryIds, ProductFilterRequest request) {

		List<String> sizes = productRepository.findSizesByCategoryIds(categoryIds);

		List<String> selectedSizes = request.getSizes();

		List<FilterOption> filters = new ArrayList<>();

		for (String size : sizes) {

			FilterOption filter = new FilterOption();

			filter.setName(size);
			filter.setChecked(selectedSizes != null && selectedSizes.contains(size));
			filters.add(filter);
		}

		return filters;
	}

	private PriceFilter getPriceFilters(List<Long> categoryIds, ProductFilterRequest request) {

		Double minPrice = null;
		Double maxPrice = null;

		PriceFilter filter = new PriceFilter();

		if (request.getMinPrice() == null && request.getMinPrice() == null) {
			minPrice = 100.0;
			maxPrice = productRepository.findMaxPrice(categoryIds);

			filter.setMinPrice(minPrice);
			filter.setMaxPrice(maxPrice);
		} else {
			filter.setMinPrice(request.getMinPrice());
			filter.setMaxPrice(request.getMaxPrice());
		}

		return filter;
	}

	private List<FilterOption> getDiscountRanges(ProductFilterRequest request) {

		List<FilterOption> ranges = new ArrayList<>();

		Integer selectedDiscount = request.getDiscount();

		for (int discount = 10; discount <= 60; discount += 10) {

			FilterOption filter = new FilterOption();

			filter.setName(discount + "% and above");
			filter.setChecked(selectedDiscount != null && selectedDiscount == discount);

			ranges.add(filter);
		}

		return ranges;
	}

	private List<BreadcrumbResponse> buildBreadcrumb(Long categoryId) {

		List<BreadcrumbResponse> breadcrumbs = new ArrayList<>();

		// Home
		BreadcrumbResponse home = new BreadcrumbResponse();
		home.setId(null);
		home.setName("Home");
		home.setSlug("/");

		breadcrumbs.add(home);

		// Get current category
		Category currentCategory = categoryRepository.findById(categoryId).orElse(null);

		if (currentCategory == null) {
			return breadcrumbs;
		}

		// Store category hierarchy
		List<Category> hierarchy = new ArrayList<>();

		Category current = currentCategory;

		while (current != null) {

			hierarchy.add(current);

			if (current.getParentId() == null) {
				break;
			}

			current = categoryRepository.findById(current.getParentId()).orElse(null);
		}

		// Reverse: T-Shirts -> Topwear -> Clothing to Clothing -> Topwear -> T-Shirts
		Collections.reverse(hierarchy);

		// Convert to breadcrumb response
		for (Category category : hierarchy) {

			BreadcrumbResponse breadcrumb = new BreadcrumbResponse();

			breadcrumb.setId(category.getId());
			breadcrumb.setName(category.getName());
			breadcrumb.setSlug(category.getSlug());

			breadcrumbs.add(breadcrumb);
		}

		return breadcrumbs;
	}

	private String buildProductUrl(Product product) {

		List<String> pathSegments = new ArrayList<>();

		// Gender
		String gender = product.getGender();

		if (gender != null && !gender.isBlank()) {
			pathSegments.add(slugify(gender));
		}

		// Category hierarchy
		Long categoryId = product.getCategoryId();

		if (categoryId != null) {

			List<Category> hierarchy = new ArrayList<>();

			Category current = categoryRepository.findById(categoryId).orElse(null);

			while (current != null) {

				hierarchy.add(current);

				if (current.getParentId() == null) {
					break;
				}

				current = categoryRepository.findById(current.getParentId()).orElse(null);
			}

			// Root to leaf
			Collections.reverse(hierarchy);

			for (Category category : hierarchy) {

				String categoryName = category.getMenuName();

				if (categoryName == null || categoryName.isBlank()) {
					categoryName = category.getName();
				}

				String slug = slugify(categoryName);

				// Skip generic root categories such as Clothing
				if (slug.equals("clothing")) {
					continue;
				}

				// Remove the gender prefix from category names,
				// e.g. "men-topwear" becomes "topwear".
				String genderPrefix = slugify(gender) + "-";

				if (!genderPrefix.equals("-") && slug.startsWith(genderPrefix)) {
					slug = slug.substring(genderPrefix.length());
				}

				if (!slug.isBlank() && !pathSegments.contains(slug)) {
					pathSegments.add(slug);
				}
			}
		}

		// Product slug
		pathSegments.add(slugify(product.getName()));

		// Product ID and buy suffix
		pathSegments.add(String.valueOf(product.getId()));
		pathSegments.add("buy");

		return String.join("/", pathSegments);
	}

	private String slugify(String value) {
		if (value == null || value.isBlank()) {
			return "";
		}

		return value.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
	}

	private ProductResponse convertToProductResponse(Product product, List<String> selectedSizes, Double minPrice,
			Double maxPrice) {

		ProductResponse response = new ProductResponse();

		response.setProductId(product.getId());
		response.setName(product.getName());
		// Generate product URL
		response.setProductUrl(buildProductUrl(product));
		response.setAddedDate(product.getAddedDate());

		// Brand
		if (product.getBrand() != null) {
			response.setBrand(product.getBrand().getName());
		}

		// Images
		response.setImages(product.getImages().stream().map(ProductImage::getImageUrl).filter(Objects::nonNull)
				.distinct().toList());

		// Variants
		if (product.getVariants() != null && !product.getVariants().isEmpty()) {

			// Available sizes
			response.setAvailableSizes(product.getVariants().stream().map(ProductVariant::getSize)
					.filter(Objects::nonNull).distinct()
					.filter(size -> selectedSizes == null || selectedSizes.isEmpty() || selectedSizes.contains(size))
					.toList());

			// Price
			ProductVariant variant = product.getVariants().stream().filter(v -> v.getPrice() != null)
					.filter(v -> minPrice == null || v.getPrice() >= minPrice)
					.filter(v -> maxPrice == null || v.getPrice() <= maxPrice)
					.min(Comparator.comparing(ProductVariant::getPrice)).orElse(null);

			if (variant != null) {

				double price = variant.getPrice();

				double offerPrice = variant.getOfferPrice();

				double discount = price > 0 ? ((price - offerPrice) / price) * 100.0 : 0.0;

				response.setPrice(price);
				response.setOfferPrice(offerPrice);
				response.setDiscount(Math.round(discount * 100.0) / 100.0);
			}

		} else {
			response.setAvailableSizes(new ArrayList<>());
			response.setImages(new ArrayList<>());
			response.setPrice(0.0);
			response.setOfferPrice(0.0);
		}

		if (product.getReviews() != null && !product.getReviews().isEmpty()) {

			List<ProductReview> approvedReviews = product.getReviews().stream()
					.filter(review -> Boolean.TRUE.equals(review.getIsApproved()))
					.filter(review -> review.getRating() != null).toList();

			// Average rating
			double averageRating = approvedReviews.stream().mapToLong(ProductReview::getRating).average().orElse(0.0);

			// Total rating count
			int totalRatingCount = approvedReviews.size();

			response.setRating(Math.round(averageRating * 100.0) / 100.0);
			response.setCount(totalRatingCount);

		}

		// These field is not present in Product entity
		response.setPopularityScore(null);

		return response;
	}

	private List<String> nullIfEmpty(List<String> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list;
	}

	public ResponseDto getProductDetails(Long productId) {

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

		// start colour building
		List<Product> colours;

		colours = productRepository.findByParentId(productId);

		if (colours.isEmpty()) {
			colours = productRepository.findByParentId(product.getParentId());
			colours.add(productRepository.findById(product.getParentId()).get());
		}

		colours.add(product);
		// end

		Category category = categoryRepository.findById(product.getCategoryId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

		ProductSeller seller = productSellerRepository.findById(product.getSellerId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

		List<ProductImage> images = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId);

		List<ProductAttribute> attributes = productAttributeRepository.findByProductIdOrderByDisplayOrderAsc(
				product.getParentId() != null ? product.getParentId() : productId);

		List<ProductReview> reviews = productReviewRepository.findByProductIdAndIsApprovedTrueOrderByReviewDateDesc(
				product.getParentId() != null ? product.getParentId() : productId);

		ProductDetailsData data = new ProductDetailsData();

		data.setBreadcrumb(buildBreadcrumb(category));
		data.setImages(images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
		data.setRating(buildRating(reviews));
		data.setPrice(buildPrice(product.getVariants()));
		data.setColor(buildColors(colours, productId));
		data.setSize(buildSizes(product, product.getVariants()));
		data.setSellerName(seller.getSellerName());

		ProductInfoDto info = new ProductInfoDto();
		info.setBrand(product.getBrand().getName());
		info.setName(product.getName());
		info.setSummary(product.getShortDescription());
		info.setDescription(product.getDescription());
		info.setProductUrl(buildProductUrl(product));
		info.setSpecification(
				attributes.stream().map(a -> new SpecificationDto(a.getAttributeName(), a.getAttributeValue()))
						.collect(Collectors.toList()));

		data.setProductDetails(info);
		data.setCustomerReview(buildReviews(reviews));

		ResponseDto response = new ResponseDto();
		response.setStatus(true);
		response.setMessage("Data fetched Successfully");
		response.setData(data);

		return response;
	}

	private List<BreadcrumbResponse> buildBreadcrumb(Category category) {

		List<Category> categories = new ArrayList<>();

		Category current = category;

		while (current != null) {
			categories.add(current);

			Long parentId = current.getParentId();

			current = parentId == null ? null : categoryRepository.findById(parentId).orElse(null);
		}

		Collections.reverse(categories);

		List<BreadcrumbResponse> breadcrumb = new ArrayList<>();

		breadcrumb.add(new BreadcrumbResponse(null, "Home", "/"));

		for (Category c : categories) {
			breadcrumb.add(new BreadcrumbResponse(c.getId(), c.getName(), c.getSlug()));
		}

		return breadcrumb;
	}

	private RatingDto buildRating(List<ProductReview> reviews) {

		RatingDto ratingDto = new RatingDto();

		long totalCount = reviews.size();

		double average = reviews.stream().mapToLong(ProductReview::getRating).average().orElse(0.0);

		ratingDto.setTotalCount(totalCount);
		ratingDto.setAverageRating(Math.round(average * 10.0) / 10.0);

		List<RatingDivisionDto> divisions = new ArrayList<>();

		for (int stars = 5; stars >= 1; stars--) {

			final int selectedStars = stars;

			long count = reviews.stream().filter(r -> r.getRating() == selectedStars).count();

			int percentage = totalCount == 0 ? 0 : (int) Math.round(count * 100.0 / totalCount);

			divisions.add(new RatingDivisionDto(stars, count, percentage));
		}

		ratingDto.setRatingDivision(divisions);

		return ratingDto;
	}

	private PriceDto buildPrice(List<ProductVariant> variants) {

		PriceDto dto = new PriceDto();

		Optional<ProductVariant> cheapest = variants.stream().filter(v -> v.getStockQuantity() > 0)
				.min(Comparator.comparing(ProductVariant::getPrice));

		if (cheapest.isEmpty()) {
			cheapest = variants.stream().min(Comparator.comparing(ProductVariant::getPrice));
		}

		if (cheapest.isEmpty()) {
			dto.setPrice(0.0);
			dto.setOfferPrice(0.0);
			dto.setDiscount(0);
			return dto;
		}

		ProductVariant variant = cheapest.get();

		double price = variant.getPrice().doubleValue();

		double offerPrice = variant.getOfferPrice() != null ? variant.getOfferPrice().doubleValue() : price;

		int discount = price > 0 && offerPrice < price ? (int) Math.round(((price - offerPrice) / price) * 100.0) : 0;

		dto.setPrice(price);
		dto.setOfferPrice(offerPrice);
		dto.setDiscount(discount);

		return dto;
	}

	private List<ColorDto> buildColors(List<Product> colours, Long productId) {

		String defaultImage = null;

		Map<String, ColorDto> colors = new LinkedHashMap<>();

		for (Product p : colours) {
			
			List<ProductImage> images = p.getImages();
			
			for (ProductImage pi : images) {
				
				if (pi.getIsPrimary()) {
					defaultImage = pi.getImageUrl();
					continue;
				}
				
			}
			
			String colorName = p.getColour();

			Long id = p.getId();

			if (colorName == null) {
				continue;
			}

			boolean selected = Objects.equals(id, productId);

			colors.putIfAbsent(colorName, new ColorDto(id, colorName, selected, defaultImage));
		}

		return new ArrayList<>(colors.values());
	}

	private SizeDataDto buildSizes(Product product, List<ProductVariant> variants) {

		SizeDataDto dto = new SizeDataDto();

		dto.setSizeChart("https://neemans.com/cdn/shop/files/Desktop_size_chart_new.jpg?v=1785406701");

		Map<String, ProductVariant> sizeMap = new LinkedHashMap<>();

		for (ProductVariant variant : variants) {
			sizeMap.putIfAbsent(variant.getSize(), variant);
		}

		List<SizeOptionDto> sizeList = new ArrayList<>();

		for (Map.Entry<String, ProductVariant> entry : sizeMap.entrySet()) {

			ProductVariant variant = entry.getValue();
			int stock = variant.getStockQuantity();

			SizeOptionDto option = new SizeOptionDto();
			option.setId(variant.getId());
			option.setSize(entry.getKey());
			option.setPrice(variant.getOfferPrice());
			option.setAvailable(stock > 0);

			if (stock <= 0) {
				option.setProduct_left("Out of stock");
			} else if (stock < 10) {
				option.setProduct_left(stock + " left");
			} else {
				option.setProduct_left(null);
			}

			sizeList.add(option);
		}

		dto.setSizeList(sizeList);

		return dto;
	}

	private List<CustomerReviewDto> buildReviews(List<ProductReview> reviews) {

		return reviews.stream().map(review -> {
			CustomerReviewDto dto = new CustomerReviewDto();

			dto.setCustomerName(review.getCustomerName());

			dto.setDate(review.getReviewDate());

			dto.setReview(review.getReviewText());
			dto.setRating(review.getRating());

			return dto;
		}).collect(Collectors.toList());
	}

}
