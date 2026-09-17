package com.astravastra.catalog_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query(value = "select * from categories where department_id=:deptId", nativeQuery = true)
	List<Category> findAllCtegoriesByDeptId(@Param("deptId") Long deptId);

	@Query(value = "SELECT * FROM categories WHERE parent_id =:parentId ORDER BY sort_order", nativeQuery = true)
	List<Category> findAllSubCtegoriesByParentId(@Param("parentId") Long parentId);
	
}
