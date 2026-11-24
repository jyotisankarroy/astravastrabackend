package com.astravastra.catalog_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astravastra.catalog_service.dto.BrandDto;
import com.astravastra.catalog_service.dto.Home1ResponseDto;
import com.astravastra.catalog_service.dto.ResponseDto;
import com.astravastra.catalog_service.repository.BrandRepository;
import com.astravastra.catalog_service.repository.CategoryRepository;

@Service
public class HomeService {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	BrandRepository brandRepository;

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

		home1Response.setLatestProductImage(
				"https://rukminim2.flixcart.com/image/612/612/xif0q/sari/s/y/w/free-tiranga9601-smc-sareemall-unstitched-original-imah8eqbkzewzq6z.jpeg?q=70");
		home1Response.setBanners(null);
		home1Response.setTopBrands(brandList);
		home1Response.setTopCategories(categoryList);

		response.setData(home1Response);
		response.setMessage("Home 1 response fetched successfully");
		response.setStatus(true);

		return response;
	}

}
