package com.diegoperalta.pos.modules.iam.application;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final long LOCK_TIME_DURATION = TimeUnit.MINUTES.toMillis(15);
    
    // Almacena username -> {intentos, timestamp_ultimo_intento}
    private final ConcurrentHashMap<String, AttemptContext> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        AttemptContext context = attemptsCache.getOrDefault(key, new AttemptContext(0, System.currentTimeMillis()));
        context.attempts++;
        context.lastAttempt = System.currentTimeMillis();
        attemptsCache.put(key, context);
    }

    public boolean isBlocked(String key) {
        AttemptContext context = attemptsCache.get(key);
        if (context == null) {
            return false;
        }
        
        // Si ya pasó el tiempo de bloqueo, lo liberamos
        if (context.attempts >= MAX_ATTEMPT && (System.currentTimeMillis() - context.lastAttempt) > LOCK_TIME_DURATION) {
            attemptsCache.remove(key);
            return false;
        }
        
        return context.attempts >= MAX_ATTEMPT;
    }

    private static class AttemptContext {
        int attempts;
        long lastAttempt;

        AttemptContext(int attempts, long lastAttempt) {
            this.attempts = attempts;
            this.lastAttempt = lastAttempt;
        }
    }
}
