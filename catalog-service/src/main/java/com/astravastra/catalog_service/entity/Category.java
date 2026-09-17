package com.astravastra.catalog_service.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    private Long id;

    private String name;
    
    @Column(name = "menu_name")
    private String menuName;
    
    private String slug;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "level_no")
    private Integer levelNo;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    private String image;
    
    private LocalDate created_at;
    
    private LocalDate updated_at;
    
}
