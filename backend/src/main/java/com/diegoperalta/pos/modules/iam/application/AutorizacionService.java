package com.diegoperalta.pos.modules.iam.application;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.modules.iam.application.dto.AutorizacionDTO;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutorizacionService {

    private final UsuarioRepository usuarioRepository;


    private final PasswordEncoder passwordEncoder;

    /**
     * Valida credenciales y permisos. Si falla, lanza excepción.
     *
     * @param dto             Credenciales del supervisor
     * @param rolesPermitidos Lista de roles que pueden autorizar (ej. "ADMIN",
     *                        "GERENTE")
     */
    public void validarAutorizacion(AutorizacionDTO dto, String... rolesPermitidos) {
        Usuario supervisor = usuarioRepository.findByUsername(dto.getUsernameSupervisor())
                .orElseThrow(() -> new BusinessException("Usuario supervisor no encontrado", HttpStatus.FORBIDDEN));

        if (!passwordEncoder.matches(dto.getPasswordSupervisor(), supervisor.getPasswordHash())) {
            throw new BusinessException("Credenciales de supervisor incorrectas", HttpStatus.FORBIDDEN);
        }

        String rolSupervisor = supervisor.getRol().getNombre();

        boolean tienePermiso = Arrays.asList(rolesPermitidos).contains(rolSupervisor);

        if (!tienePermiso) {
            throw new BusinessException(
                    "El usuario " + dto.getUsernameSupervisor() + " (Rol: " + rolSupervisor
                            + ") no tiene permiso para autorizar esta accion",
                    HttpStatus.FORBIDDEN);
        }
    }
}
