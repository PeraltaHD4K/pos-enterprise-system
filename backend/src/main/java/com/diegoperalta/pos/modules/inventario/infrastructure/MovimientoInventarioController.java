package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.inventario.application.dto.MovimientoInventarioResponseDTO;
import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("inventario/movimientos")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    
    private final MovimientoInventarioRepository movimientoRepository;

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioResponseDTO>> listarMovimientos() {
        return ResponseEntity.ok(movimientoRepository.findAllConDatos().stream().map(this::mapToDTO).toList());
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<List<MovimientoInventarioResponseDTO>> obtenerKardexProducto(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoRepository.findByProductoId(id).stream().map(this::mapToDTO).toList());
    }

    private MovimientoInventarioResponseDTO mapToDTO(MovimientoInventario mov) {
        return new MovimientoInventarioResponseDTO(
            mov.getId(),
            mov.getProducto() != null ? mov.getProducto().getId() : null,
            mov.getProducto() != null ? mov.getProducto().getNombre() : null,
            mov.getUsuario() != null ? mov.getUsuario().getId() : null,
            mov.getUsuario() != null ? mov.getUsuario().getNombreCompleto() : null,
            mov.getTipoMovimiento(),
            mov.getMotivo(),
            mov.getCantidad(),
            mov.getStockAnterior(),
            mov.getStockResultante(),
            mov.getReferencia(),
            mov.getFecha()
        );
    }
}
