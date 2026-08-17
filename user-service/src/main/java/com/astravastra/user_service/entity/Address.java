package com.astravastra.user_service.entity;

import java.util.Date;

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
	
	private String name;
	
	private String phone;

	private String address;
    
    private String address_type;
    
    private String city;
    
    private String state;
    
    private String landmark;
    
    private String pin_code;
    
    private String is_default;
    
    private Integer status;
    
    private Date created_at;
    
    private Date modified_at;

}
