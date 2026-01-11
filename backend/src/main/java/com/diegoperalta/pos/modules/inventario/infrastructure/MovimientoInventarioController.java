package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;

@RestController
@RequestMapping("inventario/movimientos")
public class MovimientoInventarioController {

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @GetMapping
    public ResponseEntity<List<MovimientoInventario>> listarMovimientos() {
        return ResponseEntity.ok(movimientoRepository.findAllConDatos());
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<List<MovimientoInventario>> obtenerKardexProducto(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoRepository.findByProductoId(id));
    }
}
