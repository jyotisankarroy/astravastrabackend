package com.astravastra.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "genders")
@Data
public class Gender {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String slug;
    
    private String code;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
}
