package com.astravastra.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.user_service.entity.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long>{

}
