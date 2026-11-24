package com.astravastra.catalog_service.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

	@Query(value = "SELECT b.id, b.name, b.logo_url, "
			+ "COUNT(DISTINCT p.id) AS total_products, "
			+ "COUNT(r.id) AS total_reviews, "
			+ "ROUND(AVG(r.rating), 2) AS avg_rating, "
			+ "ROUND(AVG(r.rating) * LOG10(COUNT(r.id) + 1), 2) AS popularity_score "
			+ "FROM brands b "
			+ "JOIN products p ON p.brand_id = b.id "
			+ "JOIN product_reviews r ON r.product_id = p.id AND r.is_approved = 1 "
			+ "WHERE b.is_active = 1 AND p.status = 'ACTIVE' "
			+ "GROUP BY b.id, b.name "
			+ "ORDER BY popularity_score DESC LIMIT 6", nativeQuery = true)
	List<Map<String, Object>> findTopBrands();
	
}
