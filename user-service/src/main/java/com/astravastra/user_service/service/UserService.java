package com.astravastra.user_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.astravastra.user_service.dto.AddressResponse;
import com.astravastra.user_service.dto.RegisterRequest;
import com.astravastra.user_service.dto.ResponseDto;
import com.astravastra.user_service.entity.Address;
import com.astravastra.user_service.entity.Users;
import com.astravastra.user_service.repository.AddressRepository;
import com.astravastra.user_service.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	
	public UserService(UserRepository userRepository, AddressRepository addressRepository) {
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
	}

	public ResponseDto getProfileByEmail(String email) {
		
		ResponseDto response = new ResponseDto();
		
		Users user = userRepository.findUserByEmail(email);

		if (user != null) {
	    	response.setData(user);
	        response.setMessage("User details fetched successfully");
	        response.setStatus(true);
	    } else {
	        response.setMessage("User not found");
	        response.setStatus(false);
	    }

	    return response;
	}
	
	public ResponseDto upsertAddress(RegisterRequest request) {

		Users existsByEmail = userRepository.findUserByEmail(request.getEmail());

		if (existsByEmail == null) {
			return new ResponseDto(false, "User not found", null);
		}

		Address address;

		// Update Address
		if (request.getAddressId() != null) {

			Optional<Address> existingAddress = addressRepository.findById(request.getAddressId());

			if (!existingAddress.isPresent()) {
				return new ResponseDto(false, "Address not found", null);
			}

			address = existingAddress.get();

			// Ownership check
			if (!address.getUser_id().equals(existsByEmail.getId())) {
				return new ResponseDto(false, "Unauthorized access to address", null);
			}

			address.setModified_at(new Date());

		} else {
			// Add Address
			address = new Address();
			address.setUser_id(existsByEmail.getId());
			address.setCreated_at(new Date());
		}

		// Common fields (both add & update)
		address.setName(request.getName());
		address.setPhone(request.getPhone());
		address.setAddress(request.getAddress());
		address.setAddress_type(request.getAddressType());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setPin_code(request.getPinCode());
		address.setLandmark(request.getLandmark());
		address.setIs_default(request.getIsDefault());

		addressRepository.save(address);

		return new ResponseDto(true,
				request.getAddressId() != null ? "Address updated successfully" : "Address added successfully", null);
	}
	
	public ResponseDto updateProfile(RegisterRequest request, String userEmail) {
		
		Users existingUser = userRepository.findUserByEmail(userEmail);

		if (existingUser == null) {
			return new ResponseDto(false, "User not found", null);
		}
		
		existingUser.setFirst_name(request.getFirstName());
		existingUser.setLast_name(request.getLastName());
		existingUser.setGender(request.getGender());
		existingUser.setDob(request.getDob());
		existingUser.setMiddle_name(request.getMiddleName());
		existingUser.setLocation(request.getLocation());
		existingUser.setPin_code(request.getPinCode());
		existingUser.setModified_at(LocalDateTime.now());
		
		userRepository.save(existingUser);
		
		return new ResponseDto(true, "Profile updated successfully", null);
	}
	
	public ResponseDto getAddresses(String email) {

	    Users userOpt = userRepository.findUserByEmail(email);

	    if (userOpt == null) {
	        return new ResponseDto(false, "User not found", null);
	    }
	    
	    List<AddressResponse> responseList = new ArrayList<>();

	    Long userId = userOpt.getId();

	    List<Address> addresses = addressRepository.findByUserId(userId);
	    
	    for (Address a : addresses) {
	    	AddressResponse response = new AddressResponse();
	    	response.setAddressId(a.getId());
	    	response.setName(a.getName());
	    	response.setPhone(a.getPhone());
	    	response.setAddress(a.getAddress());
	    	response.setAddressType(a.getAddress_type());
	    	response.setCity(a.getCity());
	    	response.setState(a.getState());
	    	response.setPinCode(a.getPin_code());
	    	response.setLandmark(a.getLandmark());
	    	response.setIsDefault(a.getIs_default());
	    	
	    	responseList.add(response);
		}
	    
	    return new ResponseDto(true, "Addresses fetched successfully", responseList);
	}

}
