package com.astravastra.authentication_service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.astravastra.authentication_service.entity.Users;
import com.astravastra.authentication_service.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users userByEmail = userRepository.findUserByEmail(username);
		
		if (userByEmail == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
		
		return userByEmail;
    }
    
}
