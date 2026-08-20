package com.astravastra.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mst_states")
@Data
public class State {

	@Id
	private Long state_id;
	
	private String state_code;
	
	private String state_name;
	
	private String region_type;

}
