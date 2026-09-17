package com.astravastra.catalog_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.Brand;
import com.astravastra.catalog_service.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("""
		    SELECT DISTINCT p
		    FROM Product p
		    LEFT JOIN p.brand b
		    LEFT JOIN p.variants v
		    WHERE p.categoryId IN :categoryIds
		      AND p.status = 'ACTIVE'
		      AND (:gender IS NULL OR LOWER(p.gender) = LOWER(:gender))
		      AND (:brandsEnabled = FALSE OR b.name IN :brands)
		      AND (:colorsEnabled = FALSE OR v.color IN :colors)
		      AND (:sizesEnabled = FALSE OR v.size IN :sizes)
		      AND (:minPrice IS NULL OR v.price >= :minPrice)
		      AND (:maxPrice IS NULL OR v.price <= :maxPrice)
		      AND (:minDiscount IS NULL OR p.discountPercentage >= :minDiscount)
		    ORDER BY p.id DESC LIMIT :limit OFFSET :offset
		    """)
		List<Product> findFilteredProducts(
		        @Param("categoryIds") List<Long> categoryIds,
		        @Param("brands") List<String> brands,
		        @Param("brandsEnabled") boolean brandsEnabled,
		        @Param("gender") String gender,
		        @Param("colors") List<String> colors,
		        @Param("colorsEnabled") boolean colorsEnabled,
		        @Param("sizes") List<String> sizes,
		        @Param("sizesEnabled") boolean sizesEnabled,
		        @Param("minPrice") Double minPrice,
		        @Param("maxPrice") Double maxPrice,
		        @Param("minDiscount") Integer minDiscount,
		        @Param("offset") int offset,
		        @Param("limit") int limit
		);

	@Query(value = "SELECT COUNT(*) FROM products WHERE category_id IN :categoryIds AND status = 'ACTIVE'", nativeQuery = true)
	Long countProductsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

	@Query(value = "SELECT DISTINCT b.* FROM products p JOIN brands b ON b.id = p.brand_id "
			+ "WHERE p.category_id IN :categoryIds AND p.status = 'ACTIVE'", nativeQuery = true)
	List<Brand> findBrandsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

	@Query(value = "SELECT DISTINCT pv.color FROM product_variants pv JOIN products p on p.id = pv.product_id "
			+ "WHERE p.category_id IN :categoryIds AND p.status = 'ACTIVE'", nativeQuery = true)
	List<String> findColorsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

	@Query(value = "SELECT DISTINCT pv.size FROM product_variants pv JOIN products p on p.id = pv.product_id "
			+ "WHERE p.category_id IN :categoryIds AND p.status = 'ACTIVE'", nativeQuery = true)
	List<String> findSizesByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

//	@Query(value = "SELECT MIN(pv.price) FROM product_variants pv JOIN products p on p.id = pv.product_id "
//			+ "WHERE p.category_id IN :categoryIds AND p.status = 'ACTIVE'", nativeQuery = true)
//	Double findMinPrice(@Param("categoryIds") List<Long> categoryIds);

	@Query(value = "SELECT MAX(pv.price) FROM product_variants pv JOIN products p on p.id = pv.product_id "
			+ "WHERE p.category_id IN :categoryIds AND p.status = 'ACTIVE'", nativeQuery = true)
	Double findMaxPrice(@Param("categoryIds") List<Long> categoryIds);

	@Query(value = "SELECT COUNT(*) FROM products WHERE category_id =:categoryId AND status = 'ACTIVE'", nativeQuery = true)
	Long countProductsByCategoryId(@Param("categoryId") Long categoryId);
	
	@Query(value = "SELECT COUNT(*) FROM products p "
			+ "JOIN product_variants v ON v.product_id = p.id WHERE v.color =:color AND p.status = 'ACTIVE'", nativeQuery = true)
	Long countProductsByColor(@Param("color") String color);
	
	@Query(value = "SELECT COUNT(*) FROM products p "
			+ "JOIN brands b ON b.id = p.brand_id WHERE b.id =:brandId AND p.status = 'ACTIVE'", nativeQuery = true)
	Long countProductsByBrandId(@Param("brandId") Integer brandId);

}
