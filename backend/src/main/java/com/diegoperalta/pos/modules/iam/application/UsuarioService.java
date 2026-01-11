package com.diegoperalta.pos.modules.iam.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.iam.application.dto.UsuarioEdicionDTO;
import com.diegoperalta.pos.modules.iam.application.dto.UsuarioRegistroDTO;
import com.diegoperalta.pos.modules.iam.domain.Rol;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.RolRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario crearUsuario(UsuarioRegistroDTO dto) {
        // 1. Validar que el username no exista
        if (usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new BusinessException("El nombre de usuario ya existe", HttpStatus.CONFLICT);
        }

        // 2. Buscar Rol
        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + dto.getRolId()));

        // 3. Crear Entidad
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setUsername(dto.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario actualizarUsuario(Long id, UsuarioEdicionDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar username duplicado (solo si cambió)
        if (!usuario.getUsername().equals(dto.getUsername()) &&
                usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);
        }

        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setUsername(dto.getUsername());

        // Solo cambiamos password si el usuario escribió algo nuevo
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        // Actualizar Rol
        if (!usuario.getRol().getId().equals(dto.getRolId())) {
            Rol nuevoRol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
            usuario.setRol(nuevoRol);
        }

        return usuarioRepository.save(usuario);
    }

    public void toggleEstadoUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setActivo(!usuario.getActivo());
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
