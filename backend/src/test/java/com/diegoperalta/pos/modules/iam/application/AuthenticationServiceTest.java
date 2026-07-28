package com.diegoperalta.pos.modules.iam.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.modules.iam.application.dto.LoginRequestDTO;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private LoginRequestDTO validRequest;
    private Usuario mockUser;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequestDTO();
        validRequest.setUsername("testuser");
        validRequest.setPassword("password123");

        mockUser = new Usuario();
        mockUser.setUsername("testuser");
    }

    @Test
    void login_Success_ReturnsJwtToken() {
        // Arrange
        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");

        // Act
        var response = authenticationService.login(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(loginAttemptService).loginSucceeded("testuser");
    }

    @Test
    void login_WhenBlocked_ThrowsBusinessException() {
        // Arrange
        when(loginAttemptService.isBlocked("testuser")).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            authenticationService.login(validRequest);
        });

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, ex.getEstado());
        assertTrue(ex.getMessage().contains("bloqueada temporalmente"));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_WhenBadCredentials_ThrowsBusinessException() {
        // Arrange
        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        doThrow(new BadCredentialsException("Bad credentials"))
            .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            authenticationService.login(validRequest);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getEstado());
        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(loginAttemptService).loginFailed("testuser");
        verify(usuarioRepository, never()).findByUsername(anyString());
    }
}
