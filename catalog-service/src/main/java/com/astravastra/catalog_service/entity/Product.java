package com.astravastra.catalog_service.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
	@Id
	private Long id;

	@Column(name = "category_id")
	private Long categoryId;

	private String name;
	
	private String description;
	
	@Column(name = "short_description")
	private String shortDescription;
	
	private String status;
	
	private String slug;
	
	private String gender;

	@Column(name = "primary_colour")
	private String primaryColour;

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "discount_percentage")
	private Integer discountPercentage;

	@Column(name = "created_at")
	private LocalDateTime addedDate;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedDate;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ProductVariant> variants;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

}
