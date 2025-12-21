package com.diegoperalta.pos.modules.iam.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.iam.application.AuthenticationService;
import com.diegoperalta.pos.modules.iam.application.dto.AuthResponseDTO;
import com.diegoperalta.pos.modules.iam.application.dto.LoginRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.login(request));
    }

}
