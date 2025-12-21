package com.diegoperalta.pos.modules.compras.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.compras.application.CompraService;
import com.diegoperalta.pos.modules.compras.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compras.domain.Compra;

@RestController
@RequestMapping("/compras")
public class CompraController {
    @Autowired
    private CompraService compraService;

    @PostMapping
    public ResponseEntity<Compra> registrarCompra(@RequestBody CompraRegistroDTO dto) {
        Compra nuevaCompra = compraService.registrarCompra(dto);
        return ResponseEntity.ok(nuevaCompra);
    }
}
