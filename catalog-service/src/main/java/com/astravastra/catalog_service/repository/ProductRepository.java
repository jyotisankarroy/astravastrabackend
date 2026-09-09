package com.astravastra.catalog_service.repository;


import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.Brand;
import com.astravastra.catalog_service.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
	@Query(value = "SELECT * FROM products WHERE category_id=:categoryId ORDER BY id LIMIT :size OFFSET :page", nativeQuery = true)
	List<Product> findByCategoryId(@Param("categoryId") Long categoryId, int size, int page);
	
	@Query(value = "SELECT COUNT(*) FROM products WHERE category_id=:categoryId", nativeQuery = true)
	Long totalCountsByCategoryId(@Param("categoryId") Long categoryId);
	
	@Query("SELECT DISTINCT p.brand FROM Product p WHERE p.categoryId = :categoryId")
	List<Brand> findDistinctBrandsByCategoryId(@Param("categoryId") Long categoryId);

	@Query("SELECT DISTINCT v.color FROM Product p JOIN p.variants v WHERE p.categoryId = :categoryId AND v.color IS NOT NULL")
	List<String> findDistinctColorsByCategoryId(@Param("categoryId") Long categoryId);

	@Query("SELECT DISTINCT v.size FROM Product p JOIN p.variants v WHERE p.categoryId = :categoryId AND v.size IS NOT NULL")
	List<String> findDistinctSizesByCategoryId(@Param("categoryId") Long categoryId);

	@Query("SELECT MIN(v.price) FROM Product p JOIN p.variants v WHERE p.categoryId = :categoryId")
	Double findMinPriceByCategoryId(@Param("categoryId") Long categoryId);

	@Query("SELECT MAX(v.price) FROM Product p JOIN p.variants v WHERE p.categoryId = :categoryId")
	Double findMaxPriceByCategoryId(@Param("categoryId") Long categoryId);
	
	@Query(value = "SELECT DISTINCT id, label FROM navigation_menus WHERE parent_id=:categoryId", nativeQuery = true)
	List<Map<String, Object>> findCategoriesByCategoryId(@Param("categoryId") Long categoryId);

}
