package com.astravastra.catalog_service.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.BannerDto;
import com.astravastra.catalog_service.dto.BrandDto;
import com.astravastra.catalog_service.dto.Home1ResponseDto;
import com.astravastra.catalog_service.dto.Home2ResponseDto;
import com.astravastra.catalog_service.dto.ProductResponse;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.entity.Category;
import com.astravastra.catalog_service.entity.Product;
import com.astravastra.catalog_service.entity.ProductImage;
import com.astravastra.catalog_service.entity.ProductVariant;
import com.astravastra.catalog_service.entity.Users;
import com.astravastra.catalog_service.repository.BrandRepository;
import com.astravastra.catalog_service.repository.CategoryRepository;
import com.astravastra.catalog_service.repository.ProductRepository;
import com.astravastra.catalog_service.repository.UserRepository;
import com.astravastra.catalog_service.repository.WishlistRepository;

@Service
public class HomeService {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	BrandRepository brandRepository;
	
	@Autowired
	ProductRepository productRepository;
	@Autowired
	WishlistRepository wishlistRepository;
	
	@Autowired
	UserRepository userRepository;

	public ResponseDto getHome1PageData() {
		ResponseDto response = new ResponseDto();

		List<Map<String, Object>> topCategories = categoryRepository.findTopCategories();

		List<Map<String, Object>> topBrands = brandRepository.findTopBrands();

		Home1ResponseDto home1Response = new Home1ResponseDto();

		List<BrandDto> brandList = new ArrayList<>();

		List<BrandDto> categoryList = new ArrayList<>();

		for (Map<String, Object> map : topBrands) {
			BrandDto dto = new BrandDto();
			dto.setId(Long.valueOf(map.get("id").toString()));
			dto.setImage(map.get("logo_url").toString());
			dto.setName(map.get("name").toString());
			dto.setPath("clothing");

			brandList.add(dto);
		}

		for (Map<String, Object> map : topCategories) {
			BrandDto dto = new BrandDto();
			dto.setId(Long.valueOf(map.get("id").toString()));
			dto.setImage(map.get("image").toString());
			dto.setName(map.get("name").toString());
			dto.setPath(map.get("slug").toString());

			categoryList.add(dto);
		}

		List<BannerDto> bannerList = Arrays.asList(createBanner("assets/Images/banner1.png", "/footwear"),
				createBanner("assets/Images/banner2.png", "/women-sportswear"),
				createBanner("assets/Images/banner3.png", "/clothing"),
				createBanner("assets/Images/banner4.png", "/women-lingerie-and-loungewear"),
				createBanner("assets/Images/banner5.png", "/men-topwear"),
				createBanner("assets/Images/banner6.png", "/women-personal-care"),
				createBanner("assets/Images/banner7.png", "/clothing?gender=Women"),
				createBanner("assets/Images/banner8.png", "/men-sportswear"),
				createBanner("assets/Images/banner9.png", "/men-topwear"));

		home1Response.setLatestProductImage(
				"https://assets.myntassets.com/h_720,q_90,w_540/v1/assets/images/2025/SEPTEMBER/24/Do3bmi5F_8a8af5f3d21b4c6d9ffeb2fbe11c3b2d.jpg");
		home1Response.setBanners(bannerList);
		home1Response.setTopBrands(brandList);
		home1Response.setTopCategories(categoryList);

		response.setData(home1Response);
		response.setMessage("Home 1 response fetched successfully");
		response.setStatus(true);

		return response;
	}
	
	private BannerDto createBanner(String image, String path) {
	    BannerDto banner = new BannerDto();
	    banner.setBannerImage(image);
	    banner.setPath(path);
	    return banner;
	}

	public ResponseDto getHome2PageData(String userEmail) {
		ResponseDto response = new ResponseDto();

		List<Map<String, Object>> trendingProducts = productRepository.findTrendingProducts();

		List<Map<String, Object>> discountedProducts = productRepository.findDiscountedProducts();

		Home2ResponseDto home2Response = new Home2ResponseDto();

		List<ProductResponse> trendingProductList = new ArrayList<>();

		List<ProductResponse> discountedProductList = new ArrayList<>();

		for (Map<String, Object> map : trendingProducts) {
			Product product = productRepository.findById(Long.valueOf(map.get("id").toString())).get();
			
			ProductResponse dto = new ProductResponse();
			
			if (userEmail != null) {
				Users user = userRepository.findUserByEmail(userEmail);
				
				boolean isWishlist = wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId());
				
				dto.setIsWishlist(isWishlist);
			}

			// Available sizes
			dto.setAvailableSizes(product.getVariants().stream().map(ProductVariant::getSize).filter(Objects::nonNull)
					.distinct().toList());
			dto.setBrand(map.get("brand_name").toString());
			dto.setDiscount(Double.valueOf(map.get("max_discount_pct").toString()));
			// Images
			dto.setImages(product.getImages().stream().map(ProductImage::getImageUrl).filter(Objects::nonNull)
					.distinct().toList());
			dto.setName(map.get("name").toString());
			dto.setOfferPrice(Double.valueOf(map.get("offer_price").toString()));
			dto.setPrice(Double.valueOf(map.get("original_price").toString()));
			dto.setProductId(Long.valueOf(map.get("id").toString()));
			dto.setProductUrl(buildProductUrl(product));
			dto.setRating(Double.valueOf(map.get("avg_rating").toString()));
			dto.setCount(Integer.valueOf(map.get("recent_reviews").toString()));

			trendingProductList.add(dto);
		}

		for (Map<String, Object> map : discountedProducts) {
			Product product = productRepository.findById(Long.valueOf(map.get("id").toString())).get();

			ProductResponse dto = new ProductResponse();
			
			if (userEmail != null) {
				Users user = userRepository.findUserByEmail(userEmail);
				
				boolean isWishlist = wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId());
				
				dto.setIsWishlist(isWishlist);
			}
			
			// Available sizes
			dto.setAvailableSizes(product.getVariants().stream().map(ProductVariant::getSize).filter(Objects::nonNull)
					.distinct().toList());
			dto.setBrand(map.get("brand_name").toString());
			dto.setDiscount(Double.valueOf(map.get("max_discount_pct").toString()));
			// Images
			dto.setImages(product.getImages().stream().map(ProductImage::getImageUrl).filter(Objects::nonNull)
					.distinct().toList());
			dto.setName(map.get("name").toString());
			dto.setOfferPrice(Double.valueOf(map.get("offer_price").toString()));
			dto.setPrice(Double.valueOf(map.get("original_price").toString()));
			dto.setProductId(Long.valueOf(map.get("id").toString()));
			dto.setProductUrl(buildProductUrl(product));
			dto.setRating(Double.valueOf(map.get("avg_rating").toString()));
			dto.setCount(Integer.valueOf(map.get("total_reviews").toString()));

			discountedProductList.add(dto);
		}

		home2Response.setTrendingProducts(trendingProductList);
		home2Response.setDiscountedProducts(discountedProductList);

		response.setData(home2Response);
		response.setMessage("Home 2 response fetched successfully");
		response.setStatus(true);

		return response;
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

}
