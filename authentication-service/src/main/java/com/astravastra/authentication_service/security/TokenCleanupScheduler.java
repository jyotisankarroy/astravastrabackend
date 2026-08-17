package com.astravastra.authentication_service.security;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.astravastra.authentication_service.repository.BlacklistedTokenRepository;

@Component
public class TokenCleanupScheduler {

    private final BlacklistedTokenRepository repository;
    
    public TokenCleanupScheduler(BlacklistedTokenRepository repository) {
		this.repository = repository;
	}

	@Scheduled(cron = "0 0 2 * * ?") // 2 AM daily
    public void cleanup() {
        repository.deleteByExpiresAtBefore(new Date());
    }
	
}
