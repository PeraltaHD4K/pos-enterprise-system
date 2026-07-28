package com.diegoperalta.pos.modules.iam.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityUserProvider implements CurrentUserProvider {


    private final UsuarioRepository usuarioRepository;

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("No hay usuario autenticado en la sesion", HttpStatus.UNAUTHORIZED);
        }
        return authentication.getName();
    }

    @Override
    public Usuario getCurrentUserDetails() {
        String username = getCurrentUsername();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en la sesión actual"));
    }
}
