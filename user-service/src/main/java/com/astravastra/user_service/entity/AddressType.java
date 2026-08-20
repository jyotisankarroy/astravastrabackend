package com.astravastra.user_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mst_address_types")
@Data
public class AddressType {

	@Id
	private Long id;
	
	private String code;
	
	private String name;
	
	private String description;

	@JsonIgnore
    private Boolean is_active;
    
}
