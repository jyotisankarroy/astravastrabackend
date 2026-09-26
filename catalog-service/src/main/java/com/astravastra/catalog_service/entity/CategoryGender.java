package com.astravastra.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "category_gender")
@Data
public class CategoryGender {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "gender_id")
    private Long genderId;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
}
