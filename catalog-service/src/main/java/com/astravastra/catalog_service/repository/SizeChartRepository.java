package com.astravastra.catalog_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.SizeChart;

@Repository
public interface SizeChartRepository extends JpaRepository<SizeChart, Long> {

	@Query(value = "SELECT sc.* FROM size_chart sc "
			+ "JOIN product_size_charts psc ON psc.size_chart_id = sc.id WHERE psc.product_id =:productId", nativeQuery = true)
	SizeChart findByProductId(@Param("productId") Long productId);

}
