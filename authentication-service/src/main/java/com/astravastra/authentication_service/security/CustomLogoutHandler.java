package com.astravastra.authentication_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.astravastra.authentication_service.entity.BlacklistedToken;
import com.astravastra.authentication_service.repository.BlacklistedTokenRepository;

import java.util.Date;

@Service
public class CustomLogoutHandler implements LogoutHandler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtService jwtService;
    
    public CustomLogoutHandler(BlacklistedTokenRepository blacklistedTokenRepository, JwtService jwtService) {
		this.blacklistedTokenRepository = blacklistedTokenRepository;
		this.jwtService = jwtService;
	}

	@Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7); // Extract token after "Bearer "

        try {
            // Extract the expiration date from the token itself
        	Date expirationDate = jwtService.extractExpiration(jwt);

            BlacklistedToken blacklistedToken = new BlacklistedToken();
            String tokenHash = DigestUtils.sha256Hex(jwt);
            blacklistedToken.setToken(tokenHash);
            blacklistedToken.setExpiresAt(expirationDate);
            
            if (!blacklistedTokenRepository.existsByToken(jwt)) {
            	blacklistedTokenRepository.save(blacklistedToken);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
