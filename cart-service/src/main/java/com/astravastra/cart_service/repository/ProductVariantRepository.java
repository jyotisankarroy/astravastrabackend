package com.astravastra.cart_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.cart_service.entity.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>{

	@Query(value = "SELECT * FROM product_variants WHERE product_id =:productId AND is_active = true", nativeQuery = true)
	List<ProductVariant> findByProductId(@Param("productId") Long productId);

}
