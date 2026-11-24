package com.astravastra.catalog_service.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.Brand;
import com.astravastra.catalog_service.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query(value = "select * from categories where department_id=:deptId", nativeQuery = true)
	List<Category> findAllCategoriesByDeptId(@Param("deptId") Long deptId);

	@Query(value = "SELECT * FROM categories WHERE parent_id =:parentId ORDER BY sort_order", nativeQuery = true)
	List<Category> findAllSubCategoriesByParentId(@Param("parentId") Long parentId);
	
	@Query(value = "SELECT * FROM categories WHERE parent_id IN (SELECT id FROM categories WHERE parent_id =:parentId)", nativeQuery = true)
	List<Category> findFinalSubCategoriesByParentId(@Param("parentId") Long parentId);
	
	@Query(value = "SELECT id FROM categories WHERE parent_id IS NULL AND is_active = 1 ORDER BY sort_order", nativeQuery = true)
	List<Long> findMainCategories();
	
	@Query(value = "SELECT * FROM categories "
			+ "WHERE parent_id IN (select c.id from categories c "
			+ "left join category_gender cg on cg.category_id = c.id "
			+ "where cg.gender_id =:genderId and parent_id =:parentId and cg.is_active = 1)", nativeQuery = true)
	List<Category> findAllCategoryByGender(@Param("genderId") Long genderId, @Param("parentId") Long parentId);

	@Query(value = "SELECT DISTINCT p.brand_id AS id, b.name, b.logo_url, b.is_active from products p "
			+ "JOIN brands b ON b.id = p.brand_id "
			+ "WHERE category_id =:categoryId", nativeQuery = true)
	List<Brand> findAllBrandsBySubCategoryId(@Param("categoryId") Long categoryId);

	@Query(value = "SELECT c.id, c.name, c.slug, c.image, "
			+ "COUNT(DISTINCT p.id) AS total_products, COUNT(r.id) AS total_reviews, "
			+ "ROUND(COALESCE(AVG(r.rating), 0), 2) AS avg_rating, "
			+ "ROUND(COALESCE(AVG(r.rating), 0) * LOG10(COUNT(r.id) + 1), 2) AS popularity_score "
			+ "FROM categories c JOIN products p ON p.category_id = c.id "
			+ "LEFT JOIN product_reviews r ON r.product_id = p.id AND r.is_approved = 1 "
			+ "WHERE c.is_active = 1 AND p.status = 'ACTIVE' AND p.parent_id IS NULL "
			+ "GROUP BY c.id, c.name ORDER BY popularity_score DESC LIMIT 12", nativeQuery = true)
	List<Map<String, Object>> findTopCategories();
	
}
