package com.astravastra.catalog_service.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.BannerDto;
import com.astravastra.catalog_service.dto.BrandDto;
import com.astravastra.catalog_service.dto.Home1ResponseDto;
import com.astravastra.catalog_service.dto.Home2ResponseDto;
import com.astravastra.catalog_service.dto.ProductResponse;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.repository.BrandRepository;
import com.astravastra.catalog_service.repository.CategoryRepository;
import com.astravastra.catalog_service.repository.ProductRepository;

@Service
public class HomeService {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	BrandRepository brandRepository;
	
	@Autowired
	ProductRepository productRepository;

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
			dto.setPath(map.get("clothing").toString());
			
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
		
		List<BannerDto> bannerList = Arrays.asList(
			    createBanner("banner1.png", "/footwear"),
			    createBanner("banner2.png", "/women-sportswear"),
			    createBanner("banner3.png", "/clothing"),
			    createBanner("banner4.png", "/women-lingerie-and-loungewear"),
			    createBanner("banner5.png", "/men-topwear")
			);

		home1Response.setLatestProductImage(
				"https://rukminim2.flixcart.com/image/612/612/xif0q/sari/s/y/w/free-tiranga9601-smc-sareemall-unstitched-original-imah8eqbkzewzq6z.jpeg?q=70");
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

	public ResponseDto getHome2PageData() {
		ResponseDto response = new ResponseDto();
		
		List<Map<String, Object>> trendingProducts = productRepository.findTrendingProducts();
		
		List<Map<String, Object>> discountedProducts = productRepository.findDiscountedProducts();
		
		Home2ResponseDto home2Response = new Home2ResponseDto();
		
		List<ProductResponse> trendingProductList = new ArrayList<>();
		
		List<ProductResponse> discountedProductList = new ArrayList<>();
		
		for (Map<String, Object> map : trendingProducts) {
			ProductResponse dto = new ProductResponse();
			dto.setAvailableSizes(null);
			dto.setBrand(map.get("brand_name").toString());
			dto.setDiscount(Double.valueOf(map.get("max_discount_pct").toString()));
			dto.setImages(null);
			dto.setIsWishlist(null);
			dto.setName(map.get("name").toString());
			dto.setOfferPrice(Double.valueOf(map.get("offer_price").toString()));
			dto.setPrice(Double.valueOf(map.get("original_price").toString()));
			dto.setProductId(Long.valueOf(map.get("id").toString()));
			dto.setProductUrl(null);
			dto.setRating(null);
			dto.setCount(Integer.valueOf(map.get("total_reviews").toString()));
			
			trendingProductList.add(dto);
		}
		
		for (Map<String, Object> map : discountedProducts) {
			ProductResponse dto = new ProductResponse();
			dto.setAvailableSizes(null);
			dto.setBrand(map.get("brand_name").toString());
			dto.setDiscount(Double.valueOf(map.get("max_discount_pct").toString()));
			dto.setImages(null);
			dto.setIsWishlist(null);
			dto.setName(map.get("name").toString());
			dto.setOfferPrice(Double.valueOf(map.get("offer_price").toString()));
			dto.setPrice(Double.valueOf(map.get("original_price").toString()));
			dto.setProductId(Long.valueOf(map.get("id").toString()));
			dto.setProductUrl(null);
			dto.setRating(null);
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

}
