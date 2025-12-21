package com.diegoperalta.pos.modules.inventario.infrastructure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.application.dto.AjusteStockDTO;
import com.diegoperalta.pos.modules.inventario.application.dto.ProductoRegistroDTO;
import com.diegoperalta.pos.modules.inventario.domain.Producto;

@RestController
@RequestMapping("/inventario/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody ProductoRegistroDTO dto) {
        Producto nuevoProducto = productoService.crearProducto(dto);
        return ResponseEntity.ok(nuevoProducto);
    }

    @PatchMapping("/{productoId}/stock")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Long productoId, @RequestBody AjusteStockDTO dto) {
        Producto producto = productoService.ajustarStock(productoId, dto.getCantidad(), "AJUSTE_MANUAL");

        return ResponseEntity.ok(producto);
    }
}
