package com.astravastra.user_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
		
		ResponseDto responseDto = new ResponseDto();

		Users existsByEmail = userRepository.findUserByEmail(userEmail);

		if (existsByEmail == null) {
			responseDto.setMessage("User not found for add/update address");
			responseDto.setStatus(false);
			return responseDto;
		}

		Address address;

		// Clear default address
		if (request.getIsDefault()) {

			List<Address> addressListByUserId = addressRepository.findAddressListByUserId(existsByEmail.getId());

			if (!addressListByUserId.isEmpty()) {

				for (Address existingAdd : addressListByUserId) {
					Boolean is_default = existingAdd.getIs_default();
					if (is_default) {
						existingAdd.setIs_default(false);
					}
				}

				addressRepository.saveAll(addressListByUserId);
			}

		}

		// Update Address
		if (request.getAddressId() != null) {

			Optional<Address> existingAddress = addressRepository.findById(request.getAddressId());

			if (!existingAddress.isPresent()) {
				responseDto.setMessage("Address not found");
				responseDto.setStatus(false);
				return responseDto;
			}

			address = existingAddress.get();

			// Ownership check
			if (!address.getUser_id().equals(existsByEmail.getId())) {
				responseDto.setMessage("Unauthorized user to update address");
				responseDto.setStatus(false);
				return responseDto;
			}

			address.setModified_at(LocalDateTime.now());

		} else {
			// Add New Address
			address = new Address();
			address.setUser_id(existsByEmail.getId());
			address.setCreated_at(LocalDateTime.now());
			address.setStatus(1);
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
		address.setIs_default(request.getIsDefault());

		addressRepository.save(address);
		
		responseDto.setMessage(request.getAddressId() != null ? "Address updated successfully" : "Address added successfully");
		responseDto.setStatus(true);
		return responseDto;

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

		ResponseDto responseDto = new ResponseDto();
		
		Users userOpt = userRepository.findUserByEmail(email);

		if (userOpt == null) {
			responseDto.setStatus(false);
			responseDto.setMessage("User not found");
			return responseDto;
		}

		List<AddressResponse> responseList = new ArrayList<>();

		Long userId = userOpt.getId();

		List<Address> addressList = addressRepository.findAddressListByUserId(userId);

		if (!addressList.isEmpty()) {
			
			for (Address address : addressList) {
				Optional<AddressType> addType = addressTypeRepository.findById(address.getAddress_type());
				AddressResponse response = new AddressResponse();
				response.setAddressId(address.getId());
				response.setFirstName(address.getFirst_name());
				response.setLastName(address.getLast_name());
				response.setPhone(address.getPhone());
				response.setAddress(address.getAddress());
				response.setAddressType(addType.get().getName());
				response.setDistrict(address.getDistrict());
				response.setState(address.getState());
				response.setPostOffice(address.getPost_office());
				response.setPinCode(address.getPin_code());
				response.setLandmark(address.getLandmark());
				response.setDefault(address.getIs_default());

				responseList.add(response);
			}
			
			responseDto.setStatus(true);
			responseDto.setMessage("Address fetched successfully");
			responseDto.setData(responseList);
		} else {
			responseDto.setStatus(false);
			responseDto.setMessage("No address found");
		}
		return responseDto;
	}

	public ResponseDto deleteAddresses(Long addressId, String userEmail) {
		
		ResponseDto responseDto = new ResponseDto();
		
		Users userOpt = userRepository.findUserByEmail(userEmail);

		if (userOpt == null) {
			responseDto.setStatus(false);
			responseDto.setMessage("User not found");
			return responseDto;
		}
		
		Address existingAddress = addressRepository.findAddressById(addressId);
		
		if (existingAddress == null) {
			responseDto.setStatus(false);
			responseDto.setMessage("Address not found");
			return responseDto;
		}

		// Ownership check
		if (!existingAddress.getUser_id().equals(userOpt.getId())) {
			responseDto.setStatus(false);
			responseDto.setMessage("Unauthorized user to delete address");
			return responseDto;
		}
		
		existingAddress.setStatus(0);
		addressRepository.save(existingAddress);
		
		responseDto.setStatus(true);
		responseDto.setMessage("Address deleted successfully");
		return responseDto;
	}

}
