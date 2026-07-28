package com.diegoperalta.pos.modules.iam.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.iam.application.AuthenticationService;
import com.diegoperalta.pos.modules.iam.application.LoginRateLimiterService;
import com.diegoperalta.pos.modules.iam.application.dto.AuthResponseDTO;
import com.diegoperalta.pos.modules.iam.application.dto.LoginRequestDTO;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService service;
    private final LoginRateLimiterService rateLimiterService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request, HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();

        if (!rateLimiterService.isAllowed(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos fallidos. Intente de nuevo en 1 minuto.");
        }

        try {
            AuthResponseDTO response = service.login(request);
            rateLimiterService.resetAttempts(ip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            rateLimiterService.recordFailedAttempt(ip);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            service.logout(jwt);
        }
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Sesión cerrada exitosamente"));
    }

}
