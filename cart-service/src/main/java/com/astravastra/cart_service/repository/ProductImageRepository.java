package com.astravastra.cart_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.astravastra.cart_service.entity.ProductImage;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	List<ProductImage> findByVariant_IdOrderByDisplayOrderAsc(Long variantId);
	
}
