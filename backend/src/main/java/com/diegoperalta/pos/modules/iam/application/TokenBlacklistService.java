package com.diegoperalta.pos.modules.iam.application;

import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.iam.domain.TokenBlacklist;
import com.diegoperalta.pos.modules.iam.infrastructure.TokenBlacklistRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtService jwtService;

    public void blacklistToken(String token) {
        Date expirationDate = jwtService.extractExpiration(token);
        TokenBlacklist blacklistedToken = new TokenBlacklist(token, expirationDate.toInstant());
        tokenBlacklistRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsById(token);
    }
}
