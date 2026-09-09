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

	@Column(name = "popularity_score")
	private Integer popularityScore;

	@Column(name = "customer_rating")
	private Double customerRating;

	@Column(name = "discount_percentage")
	private Integer discountPercentage;

	@Column(name = "added_date")
	private LocalDateTime addedDate;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ProductVariant> variants;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ProductImage> images;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

}
