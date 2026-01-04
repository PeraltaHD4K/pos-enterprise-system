package com.diegoperalta.pos.modules.iam.application;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.iam.application.dto.AuthResponseDTO;
import com.diegoperalta.pos.modules.iam.application.dto.LoginRequestDTO;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Autenticar con Spring Security (Esto valida usuario y contraseña
        // automáticamente)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        // 2. Si pasó el paso anterior, el usuario es correcto. Lo buscamos.
        var user = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // 3. Generamos el Token
        var jwtToken = jwtService.generateToken(user);

        return new AuthResponseDTO(jwtToken);
    }

}
