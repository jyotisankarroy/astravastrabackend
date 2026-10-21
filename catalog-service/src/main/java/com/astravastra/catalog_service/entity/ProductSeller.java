package com.astravastra.catalog_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product_sellers")
@Data
public class ProductSeller {
	
	@Id
    private Long id;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "seller_code")
    private String sellerCode;
    
    @Column(name = "is_active")
    private Boolean isActive;

}
