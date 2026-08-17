package com.astravastra.gateway_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
	    return http
	        .csrf(ServerHttpSecurity.CsrfSpec::disable)
	        .authorizeExchange(exchanges -> exchanges
	            .pathMatchers("/actuator/**").permitAll()
	            .pathMatchers("/api/auth/**").permitAll()
	            .anyExchange().authenticated()
	        )
	        .build();
	}

}
