package com.astravastra.catalog_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product_reviews")
@Data
public class ProductReview {
	
	@Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "customer_name")
    private String customerName;
    
    private Long rating;

    @Column(name = "review_text")
    private String reviewText;
    
    @Column(name = "review_date")
    private String reviewDate;

    @Column(name = "is_approved")
    private Boolean isApproved;

}
