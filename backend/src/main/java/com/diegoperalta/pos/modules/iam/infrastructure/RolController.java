package com.diegoperalta.pos.modules.iam.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.iam.application.RolService;
import com.diegoperalta.pos.modules.iam.domain.Rol;

@RestController
@RequestMapping("/roles")
public class RolController {
    @Autowired
    private RolService rolService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(rolService.listarTodos());
    }
}
