package com.astravastra.user_service.dto;

import lombok.Data;

@Data
public class AddressResponse {

    private long addressId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String addressType;
    private String city;
    private String state;
    private long pinCode;
    private String landmark;
    private boolean isDefault;
    
}
