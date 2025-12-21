package com.diegoperalta.pos.modules.ventas.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.ventas.application.VentaService;
import com.diegoperalta.pos.modules.ventas.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.ventas.domain.Venta;

@RestController
@RequestMapping("/ventas")
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody VentaRegistroDTO dto) {
        Venta nuevaVenta = ventaService.registrarVenta(dto);
        return ResponseEntity.ok(nuevaVenta);
    }
}
