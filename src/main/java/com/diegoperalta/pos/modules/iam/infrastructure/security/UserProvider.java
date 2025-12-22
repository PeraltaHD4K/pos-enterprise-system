package com.diegoperalta.pos.modules.iam.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.diegoperalta.pos.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

@Component
public class UserProvider {
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("No hay usuario autenticado en la sesion", HttpStatus.UNAUTHORIZED);
        }
        return authentication.getName();
    }
}
