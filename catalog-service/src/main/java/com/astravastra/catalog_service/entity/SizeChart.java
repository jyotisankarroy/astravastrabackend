package com.astravastra.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "size_chart")
@Data
public class SizeChart {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @Column(name = "size_chart_image")
    private String sizeChart;
    
}
