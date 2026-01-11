package com.diegoperalta.pos.modules.iam.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.iam.application.UsuarioService;
import com.diegoperalta.pos.modules.iam.application.dto.UsuarioEdicionDTO;
import com.diegoperalta.pos.modules.iam.application.dto.UsuarioRegistroDTO;
import com.diegoperalta.pos.modules.iam.domain.Usuario;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')") // Solo jefes ven la lista
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo el ADMIN crea empleados
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioRegistroDTO dto) {
        return ResponseEntity.ok(usuarioService.crearUsuario(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioEdicionDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id) {
        usuarioService.toggleEstadoUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
