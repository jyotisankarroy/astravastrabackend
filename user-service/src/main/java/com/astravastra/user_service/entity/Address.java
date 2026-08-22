package com.astravastra.user_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "addresses")
@Data
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long user_id;
	
	private String first_name;
	
	private String last_name;
	
	private String phone;

	private String address;
    
    private Long address_type;
    
    private String district;
    
    private String state;
    
    private String post_office;
    
    private String landmark;
    
    private Long pin_code;
    
    private String country;
    
    private Boolean is_default;
    
    private Integer status;
    
    private LocalDateTime created_at;
    
    private LocalDateTime modified_at;

}
