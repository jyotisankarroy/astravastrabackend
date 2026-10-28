package com.astravastra.catalog_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
	
	List<ProductImage> findByProduct_IdOrderByDisplayOrderAsc(Long productId);

}
