package com.astravastra.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    private Long id;

    private String name;
    
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentId;

    @Column(name = "seo_title")
    private String seoTitle;

    @Column(name = "seo_description")
    private String seoDescription;
    
}
