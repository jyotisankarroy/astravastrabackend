package com.astravastra.catalog_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.ProductAttribute;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

	List<ProductAttribute> findByProductIdOrderByDisplayOrderAsc(Long productId);

}
