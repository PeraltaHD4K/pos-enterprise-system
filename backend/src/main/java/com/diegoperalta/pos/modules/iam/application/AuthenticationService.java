package com.diegoperalta.pos.modules.iam.application;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.iam.application.dto.AuthResponseDTO;
import com.diegoperalta.pos.modules.iam.application.dto.LoginRequestDTO;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername();

        if (loginAttemptService.isBlocked(username)) {
            throw new com.diegoperalta.pos.common.exception.BusinessException("Cuenta bloqueada temporalmente por múltiples intentos fallidos", org.springframework.http.HttpStatus.TOO_MANY_REQUESTS);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));
            loginAttemptService.loginSucceeded(username);
        } catch (AuthenticationException e) {
            loginAttemptService.loginFailed(username);
            throw new com.diegoperalta.pos.common.exception.BusinessException("Credenciales inválidas", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        // 2. Si pasó el paso anterior, el usuario es correcto. Lo buscamos.
        var user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new com.diegoperalta.pos.common.exception.ResourceNotFoundException("Usuario no encontrado en base de datos"));

        // 3. Generamos el Token
        var securityUser = new com.diegoperalta.pos.modules.iam.infrastructure.security.SecurityUser(user);
        var jwtToken = jwtService.generateToken(securityUser);

        return new AuthResponseDTO(jwtToken);
    }

    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
    }

}
