package com.diegoperalta.pos.modules.compras.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.compras.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compras.application.dto.ItemCompraDTO;
import com.diegoperalta.pos.modules.compras.domain.Compra;
import com.diegoperalta.pos.modules.compras.domain.DetalleCompra;
import com.diegoperalta.pos.modules.compras.infrastructure.CompraRepository;
import com.diegoperalta.pos.modules.compras.infrastructure.ProveedorRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;

@Service
public class CompraService {
    @Autowired
    private CompraRepository compraRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private UserProvider userProvider;

    @Transactional
    public Compra registrarCompra(CompraRegistroDTO dto) {
        Compra compra = new Compra();

        // Validaciones básicas
        compra.setProveedor(proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado")));

        compra.setUsuario(obtenerUsuarioActual());
        compra.setFolioFactura(dto.getFolioFactura());
        compra.setEstado("COMPLETADA");
        compra.setDetalles(new ArrayList<>());
        compra.setFechaCompra(LocalDateTime.now());

        BigDecimal totalCompra = BigDecimal.ZERO;

        for (ItemCompraDTO item : dto.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Producto no encontrado: " + item.getProductoId()));

            // 1. Crear Detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setCostoUnitario(item.getCostoUnitario());

            compra.getDetalles().add(detalle);

            // Sumar al total de la factura
            BigDecimal subtotal = item.getCostoUnitario().multiply(new BigDecimal(item.getCantidad()));
            totalCompra = totalCompra.add(subtotal);

            // 2. IMPACTAR INVENTARIO (Magia del Costo Promedio)
            productoService.registrarEntradaPorCompra(
                    producto.getId(),
                    item.getCantidad(),
                    item.getCostoUnitario());
        }

        compra.setTotal(totalCompra);
        return compraRepository.save(compra);
    }

    private Usuario obtenerUsuarioActual() {
        String username = userProvider.getCurrentUser();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en la sesión actual"));
    }
}
