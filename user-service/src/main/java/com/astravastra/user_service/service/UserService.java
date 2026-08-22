package com.astravastra.user_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.astravastra.user_service.dto.AddressResponse;
import com.astravastra.user_service.dto.RegisterRequest;
import com.astravastra.user_service.dto.ResponseDto;
import com.astravastra.user_service.dto.UserDetails;
import com.astravastra.user_service.entity.Address;
import com.astravastra.user_service.entity.AddressType;
import com.astravastra.user_service.entity.Users;
import com.astravastra.user_service.repository.AddressRepository;
import com.astravastra.user_service.repository.AddressTypeRepository;
import com.astravastra.user_service.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	private final AddressTypeRepository addressTypeRepository;
	
	public UserService(UserRepository userRepository, AddressRepository addressRepository,
			AddressTypeRepository addressTypeRepository) {
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
		this.addressTypeRepository = addressTypeRepository;
	}
	
	public ResponseDto getAddressType() {
		List<AddressType> typeList = addressTypeRepository.findAllAddressTypes();
		ResponseDto response = new ResponseDto();
		if (!typeList.isEmpty()) {
			response.setStatus(true);
			response.setMessage("data fetched successfully");
			response.setData(typeList);
		} else {
			response.setStatus(false);
			response.setMessage("unable to fetch data");
		}
		
		return response;
	}

	public ResponseDto getProfileByEmail(String email) {
		
		ResponseDto response = new ResponseDto();
		
		Users user = userRepository.findUserByEmail(email);

		if (user != null) {
			UserDetails details = new UserDetails();
			details.setFirstName(user.getFirst_name());
			details.setLastName(user.getLast_name());
			details.setEmail(user.getEmail());
			details.setPhone(user.getPhone());
			details.setGender(user.getGender());
			details.setDob(user.getDob());
			details.setLocation(user.getLocation());
			details.setPinCode(user.getPin_code());
	    	response.setData(details);
	        response.setMessage("User details fetched successfully");
	        response.setStatus(true);
	    } else {
	        response.setMessage("User not found");
	        response.setStatus(false);
	    }

	    return response;
	}
	
	public ResponseDto upsertAddress(RegisterRequest request, String userEmail) {

		Users existsByEmail = userRepository.findUserByEmail(userEmail);

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

			address.setModified_at(LocalDateTime.now());

		} else {
			// Add Address
			address = new Address();
			address.setUser_id(existsByEmail.getId());
			address.setCreated_at(LocalDateTime.now());
			address.setStatus(0);
			address.setCountry("India");
		}

		// Common fields (both add & update)
		address.setFirst_name(request.getFirstName());
		address.setLast_name(request.getLastName());
		address.setPhone(request.getPhone());
		address.setAddress(request.getAddress());
		address.setAddress_type(request.getAddressType());
		address.setDistrict(request.getDistrict());
		address.setPost_office(request.getPostOffice());
		address.setState(request.getState());
		address.setPin_code(request.getPinCode());
		address.setLandmark(request.getLandmark());
		address.setIs_default(request.isDefault());

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

	    List<Map<String, Object>> addressList = addressRepository.findAddressListByUserId(userId);
	    
	    for (Map<String, Object> address : addressList) {
	    	AddressResponse response = new AddressResponse();
	    	response.setAddressId(Long.parseLong(address.get("id").toString()));
	    	response.setFirstName(address.get("first_name").toString());
	    	response.setLastName(address.get("last_name").toString());
	    	response.setPhone(address.get("phone").toString());
	    	response.setAddress(address.get("address").toString());
	    	response.setAddressType(address.get("addType").toString());
	    	response.setDistrict(address.get("district").toString());
	    	response.setState(address.get("state").toString());
	    	response.setPostOffice(address.get("post_office").toString());
	    	response.setPinCode(Long.parseLong(address.get("pin_code").toString()));
	    	response.setLandmark(address.get("landmark").toString());
	    	Object value = address.get("is_default");
	    	response.setDefault(value != null && ((Byte) value) == 1);
	    	
	    	responseList.add(response);
		}
	    
	    return new ResponseDto(true, "Addresses fetched successfully", responseList);
	}

}
