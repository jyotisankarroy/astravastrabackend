package com.astravastra.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    private String name;

    private String slug;
}
