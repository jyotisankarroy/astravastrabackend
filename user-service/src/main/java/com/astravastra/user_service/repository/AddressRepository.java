package com.astravastra.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.user_service.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
	
	@Query(value = "SELECT * FROM addresses WHERE user_id=:userId AND status=1", nativeQuery = true)
	List<Address> findAddressListByUserId(@Param("userId") long userId);

	@Query(value = "SELECT * FROM addresses WHERE id=:addressId AND status=1", nativeQuery = true)
	Address findAddressById(@Param("addressId") long addressId);

}
