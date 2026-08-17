package com.astravastra.authentication_service.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.authentication_service.entity.BlacklistedToken;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
	
    boolean existsByToken(String token);

	void deleteByExpiresAtBefore(Date date);
    
}
