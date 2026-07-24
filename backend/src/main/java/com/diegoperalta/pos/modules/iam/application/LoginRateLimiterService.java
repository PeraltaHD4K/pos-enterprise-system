package com.diegoperalta.pos.modules.iam.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginRateLimiterService {
    private final ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> lockouts = new ConcurrentHashMap<>();

    public boolean isAllowed(String ip) {
        Instant lockout = lockouts.get(ip);
        if (lockout != null && Instant.now().isBefore(lockout)) {
            return false;
        } else if (lockout != null) {
            lockouts.remove(ip);
            attempts.remove(ip);
        }

        int currentAttempts = attempts.getOrDefault(ip, 0);
        if (currentAttempts >= 5) {
            lockouts.put(ip, Instant.now().plus(1, ChronoUnit.MINUTES));
            return false;
        }
        
        return true;
    }

    public void recordFailedAttempt(String ip) {
        attempts.put(ip, attempts.getOrDefault(ip, 0) + 1);
    }

    public void resetAttempts(String ip) {
        attempts.remove(ip);
        lockouts.remove(ip);
    }
}
