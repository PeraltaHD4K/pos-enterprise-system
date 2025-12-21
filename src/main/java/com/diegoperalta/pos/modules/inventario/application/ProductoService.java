package com.diegoperalta.pos.modules.inventario.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.inventario.application.dto.ProductoRegistroDTO;
import com.diegoperalta.pos.modules.inventario.domain.Categoria;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.CategoriaRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Producto crearProducto(ProductoRegistroDTO dto) {
        // 1. Validar que la categoría exista
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(dto.getCategoriaId());

        if (categoriaOptional.isEmpty()) {
            throw new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId());
        }

        // 2. Convertir DTO a Entidad
        Producto producto = new Producto();
        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setCostoPromedio(dto.getCostoPromedio());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setStockActual(0);

        // 3. Asignar la relacion con la categoria
        producto.setCategoria(categoriaOptional.get());

        // 4. Guardar el producto
        return productoRepository.save(producto);
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }
}
