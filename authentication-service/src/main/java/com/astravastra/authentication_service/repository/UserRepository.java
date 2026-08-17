package com.astravastra.authentication_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.authentication_service.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

	@Query(value = "SELECT * FROM users WHERE email=:email AND is_active = true", nativeQuery = true)
	Users findUserByEmail(@Param("email") String email);
	
}
