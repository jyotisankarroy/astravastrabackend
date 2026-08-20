package com.astravastra.user_service.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.user_service.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
	
	@Query(value = "SELECT a.*, at.name AS addType, s.state_name AS stName "
			+ "FROM addresses a "
			+ "JOIN mst_address_types at ON at.id = a.address_type "
			+ "JOIN mst_states s ON s.state_id = a.state "
			+ "WHERE user_id=:userId", nativeQuery = true)
	List<Map<String, Object>> findAddressListByUserId(@Param("userId") long userId);

}
