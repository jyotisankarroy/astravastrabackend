package com.astravastra.authentication_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astravastra.authentication_service.entity.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long>{

	@Modifying
    @Query(value = "UPDATE otp_records SET is_used = true WHERE email=:email AND is_used = false", nativeQuery = true)
    void invalidatePreviousOtps(@Param("email") String email);

	@Query(value = "SELECT * FROM otp_records WHERE email=:email AND otp_code=:otp AND is_used = false", nativeQuery = true)
	Otp findByEmailAndOtpCodeAndIsUsedFalse(@Param("email") String email, @Param("otp") String otp);

}
