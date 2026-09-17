package com.astravastra.catalog_service.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "departments")
@Data
public class Department {
	
	@Id
    private Long id;

    private String name;
    
    private String slug;
    
    private Integer sort_order;
    
    private Boolean is_active;
    
    private LocalDate created_at;
    
    private LocalDate updated_at;

}
