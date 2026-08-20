package com.astravastra.user_service.entity;

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

    private Boolean is_active;
    
}
