package com.astravastra.user_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	private LocalDateTime created_at;

    private String email;

    private String first_name;
    
    private String gender;

    private String location;
    
    private Long pin_code;

    private String last_name;
    
    private String dob;

    private LocalDateTime modified_at;

    private String phone;

    private Integer status;

    private Boolean is_active;

}
