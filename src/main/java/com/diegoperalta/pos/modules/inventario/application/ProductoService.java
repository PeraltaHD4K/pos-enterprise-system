package com.diegoperalta.pos.modules.inventario.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.inventario.application.dto.ProductoRegistroDTO;
import com.diegoperalta.pos.modules.inventario.domain.Categoria;
import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.CategoriaRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.MovimientoInventarioRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @Transactional
    public Producto ajustarStock(Long productoId, Integer cantidad, String tipoMovimiento) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        Usuario usuario = usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario Administrador no encontrado en BD"));

        int stockAnterior = producto.getStockActual() == null ? 0 : producto.getStockActual();
        int stockResultante = stockAnterior + cantidad;

        if (stockResultante < 0) {
            throw new RuntimeException("No se puede ajustar el stock a un valor negativo");
        }

        producto.setStockActual(stockResultante);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockResultante(stockResultante);

        movimientoRepository.save(movimiento);

        return producto;
    }
}
