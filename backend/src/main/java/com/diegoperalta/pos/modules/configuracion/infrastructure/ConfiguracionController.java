package com.diegoperalta.pos.modules.configuracion.infrastructure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.configuracion.application.ConfiguracionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfiguracionController {

    
    private final ConfiguracionService configService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<Map<String, String>> obtener() {
        return ResponseEntity.ok(configService.obtenerConfiguracionCompleta());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> guardar(@RequestBody Map<String, String> config) {
        configService.actualizarConfiguracion(config);
        return ResponseEntity.ok().build();
    }

}
