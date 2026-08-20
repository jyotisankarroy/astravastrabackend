package com.astravastra.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.astravastra.user_service.entity.AddressType;

@Repository
public interface AddressTypeRepository extends JpaRepository<AddressType, Long>{

	@Query(value = "SELECT * FROM mst_address_types WHERE is_active = true", nativeQuery = true)
	List<AddressType> findAllAddressTypes();

}
