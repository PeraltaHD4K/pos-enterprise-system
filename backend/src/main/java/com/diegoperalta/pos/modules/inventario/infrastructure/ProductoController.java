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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;

import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.application.dto.AjusteStockDTO;
import com.diegoperalta.pos.modules.inventario.application.dto.ProductoRegistroDTO;
import com.diegoperalta.pos.modules.inventario.application.dto.ProductoResponseDTO;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventario/productos")
@RequiredArgsConstructor
public class ProductoController {
    
    private final ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<Page<ProductoResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.listarTodos(pageable).map(this::mapToDTO));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRegistroDTO dto) {
        Producto nuevoProducto = productoService.crearProducto(dto);
        return ResponseEntity.ok(mapToDTO(nuevoProducto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(productoService.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRegistroDTO dto) {
        return ResponseEntity.ok(mapToDTO(productoService.actualizarProducto(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productoId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<ProductoResponseDTO> actualizarStock(@PathVariable Long productoId,
            @Valid @RequestBody AjusteStockDTO dto) {
        Producto producto = productoService.ajustarStock(productoId, dto);

        return ResponseEntity.ok(mapToDTO(producto));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<List<ProductoResponseDTO>> buscarProductos(@RequestParam("q") String query) {
        List<Producto> resultados = productoService.buscarProductos(query);
        return ResponseEntity.ok(resultados.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/reportes/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerStockBajo() {
        List<Producto> productosConStockBajo = productoService.obtenerReporteStockBajo();
        return ResponseEntity.ok(productosConStockBajo.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    private ProductoResponseDTO mapToDTO(Producto p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(p.getId());
        dto.setSku(p.getSku());
        dto.setCodigoBarras(p.getCodigoBarras());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecioVenta(p.getPrecioVenta());
        dto.setCostoPromedio(p.getCostoPromedio());
        dto.setUltimoCostoCompra(p.getUltimoCostoCompra());
        dto.setStockActual(p.getStockActual());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setActivo(p.getActivo());
        if (p.getCategoria() != null) {
            dto.setCategoriaNombre(p.getCategoria().getNombre());
            dto.setCategoriaId(p.getCategoria().getId());
        }
        return dto;
    }
}
