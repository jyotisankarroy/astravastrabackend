package com.astravastra.catalog_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product_attributes")
@Data
public class ProductAttribute {
	
	@Id
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "attribute_name")
    private String attributeName;

    @Column(name = "attribute_value")
    private String attributeValue;

    @Column(name = "display_order")
    private Integer displayOrder;
    
}
