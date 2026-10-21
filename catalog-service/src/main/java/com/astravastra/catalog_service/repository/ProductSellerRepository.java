package com.astravastra.catalog_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astravastra.catalog_service.entity.ProductSeller;

@Repository
public interface ProductSellerRepository extends JpaRepository<ProductSeller, Long> {

}
