package com.diegoperalta.pos.modules.compras.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.compras.application.CompraService;
import com.diegoperalta.pos.modules.compras.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compras.domain.Compra;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/compras")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class CompraController {
    @Autowired
    private CompraService compraService;

    @Autowired
    private CompraRepository compraRepository;

    @PostMapping
    public ResponseEntity<Compra> registrarCompra(@Valid @RequestBody CompraRegistroDTO dto) {
        Compra nuevaCompra = compraService.registrarCompra(dto);
        return ResponseEntity.ok(nuevaCompra);
    }

    @PostMapping("/confirmar/{id}")
    public ResponseEntity<Compra> confirmarRecepcion(@PathVariable Long id) {
        Compra compraConfirmada = compraService.confirmarRecepcion(id);
        return ResponseEntity.ok(compraConfirmada);
    }

    @GetMapping
    public ResponseEntity<List<Compra>> listarCompras() {
        return ResponseEntity.ok(compraRepository.findAllConDatos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtenerPorId(@PathVariable Long id) {
        return compraRepository.findByIdConDetalles(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
