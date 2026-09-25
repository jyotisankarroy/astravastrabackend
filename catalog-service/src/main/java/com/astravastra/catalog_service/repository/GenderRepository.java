package com.astravastra.catalog_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.Gender;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Long> {

	@Query(value = "select * from genders where is_active = 1", nativeQuery = true)
	List<Gender> findAllGender();
	
}
