package com.astravastra.catalog_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

	List<ProductVariant> findByProduct_IdAndActiveTrue(Long productId);

}
