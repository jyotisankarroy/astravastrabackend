package com.astravastra.catalog_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.astravastra.catalog_service.entity.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

	List<ProductReview> findByProductIdAndIsApprovedTrueOrderByReviewDateDesc(Long productId);
	
}
