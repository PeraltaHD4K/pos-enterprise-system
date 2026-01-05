package com.diegoperalta.pos.modules.inventario.infrastructure;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRegistroDTO dto) {
        Producto nuevoProducto = productoService.crearProducto(dto);
        return ResponseEntity.ok(nuevoProducto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRegistroDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productoId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Long productoId,
            @Valid @RequestBody AjusteStockDTO dto) {
        Producto producto = productoService.ajustarStock(productoId, dto);

        return ResponseEntity.ok(producto);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam("q") String query) {
        List<Producto> resultados = productoService.buscarProductos(query);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/reportes/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Producto>> obtenerStockBajo() {
        List<Producto> productosConStockBajo = productoService.obtenerReporteStockBajo();
        return ResponseEntity.ok(productosConStockBajo);
    }
}
