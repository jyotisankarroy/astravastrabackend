package com.astravastra.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "navigation_menus")
@Data
public class NavigationMenu {

    @Id
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    private String label;

    @Column(name = "target_category_id")
    private Long categoryId;

    @Column(name = "display_order")
    private Long displayOrder;
    
    private String slug;
    
    private String image;
    
}
