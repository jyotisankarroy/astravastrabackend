package com.astravastra.catalog_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.NavigationMenu;

import java.util.List;

@Repository
public interface NavigationMenuRepository extends JpaRepository<NavigationMenu, Integer> {
    
    List<NavigationMenu> findAllByOrderByDisplayOrderAsc();
    
}
