package com.diegoperalta.pos.modules.compras.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.compras.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compras.application.dto.ItemCompraDTO;
import com.diegoperalta.pos.modules.compras.domain.Compra;
import com.diegoperalta.pos.modules.compras.domain.DetalleCompra;
import com.diegoperalta.pos.modules.compras.domain.Proveedor;
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
        // Validaciones básicas
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("La compra debe incluir al menos un producto", HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = obtenerUsuarioActual();

        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + dto.getProveedorId()));

        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setUsuario(usuario);
        compra.setFolioFactura(dto.getFolioFactura());
        compra.setObservaciones(dto.getObservaciones());

        compra.setFechaPedido(LocalDateTime.now());
        compra.setFechaEstimadaEntrega(dto.getFechaEstimadaEntrega());

        String estadoInicial = (dto.getEstado() != null) ? dto.getEstado() : "COMPLETADA";
        compra.setEstado(estadoInicial);

        if ("COMPLETADA".equals(estadoInicial)) {
            compra.setFechaRecepcion(LocalDateTime.now());
        }

        BigDecimal totalCompra = BigDecimal.ZERO;
        compra.setDetalles(new ArrayList<>());

        for (ItemCompraDTO item : dto.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Producto no encontrado ID: " + item.getProductoId()));

            // 1. Crear Detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);

            detalle.setCantidadPedida(item.getCantidadPedida());
            detalle.setUnidadesPorCaja(item.getUnidadesPorCaja() != null ? item.getUnidadesPorCaja() : 1);

            if (item.getCostoTotal() != null) {
                detalle.setCostoTotalRenglon(item.getCostoTotal());
            } else {
                BigDecimal ultimoCosto = producto.getUltimoCostoCompra();
                if (ultimoCosto != null && ultimoCosto.compareTo(BigDecimal.ZERO) > 0) {
                    int totalPiezas = item.getCantidadPedida()
                            * (item.getUnidadesPorCaja() != null ? item.getUnidadesPorCaja() : 1);

                    BigDecimal estimado = ultimoCosto.multiply(new BigDecimal(totalPiezas));
                    detalle.setCostoTotalRenglon(estimado);
                }
            }

            if (detalle.getCostoTotalRenglon() != null) {
                totalCompra = totalCompra.add(detalle.getCostoTotalRenglon());
            }

            // 2. IMPACTAR INVENTARIO
            if ("COMPLETADA".equals(estadoInicial)) {
                int recibida = item.getCantidadRecibida() != null ? item.getCantidadRecibida()
                        : item.getCantidadPedida();
                detalle.setCantidadRecibida(recibida);

                if (detalle.getCostoTotalRenglon() != null && recibida > 0) {
                    BigDecimal totalPiezas = new BigDecimal(recibida * detalle.getUnidadesPorCaja());
                    BigDecimal costoUnitario = detalle.getCostoTotalRenglon().divide(totalPiezas, 4,
                            RoundingMode.HALF_UP);

                    detalle.setCostoUnitarioCalculado(costoUnitario);

                    productoService.registrarEntradaPorCompra(
                            producto.getId(),
                            totalPiezas.intValue(),
                            costoUnitario,
                            compra.getId());
                }
            }
            compra.getDetalles().add(detalle);
        }

        compra.setTotal(totalCompra);
        return compraRepository.save(compra);
    }

    @Transactional
    public Compra confirmarRecepcion(Long compraId) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));

        if (!"PENDIENTE".equals(compra.getEstado())) {
            throw new BusinessException("Solo se puede confirmar la recepción de una compra PENDIENTE",
                    HttpStatus.CONFLICT);
        }

        // Aquí asumimos que llegó TODO lo pedido.
        // (Para la versión avanzada, el endpoint debería recibir un DTO con las
        // diferencias,
        // pero por ahora cerraremos asumiendo éxito total para simplificar).

        BigDecimal totalReal = BigDecimal.ZERO;

        for (DetalleCompra detalle : compra.getDetalles()) {
            detalle.setCantidadRecibida(detalle.getCantidadPedida());

            if (detalle.getCostoTotalRenglon() != null) {
                BigDecimal totalPiezas = new BigDecimal(detalle.getTotalPiezasReales());
                if (totalPiezas.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal unitario = detalle.getCostoTotalRenglon().divide(totalPiezas, 4, RoundingMode.HALF_UP);
                    detalle.setCostoUnitarioCalculado(unitario);

                    productoService.registrarEntradaPorCompra(
                            detalle.getProducto().getId(),
                            totalPiezas.intValue(),
                            unitario,
                            compra.getId());
                }
                totalReal = totalReal.add(detalle.getCostoTotalRenglon());
            }
        }

        compra.setEstado("COMPLETADA");
        compra.setFechaRecepcion(LocalDateTime.now());
        compra.setTotal(totalReal);

        return compraRepository.save(compra);
    }

    private Usuario obtenerUsuarioActual() {
        String username = userProvider.getCurrentUser();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en la sesión actual"));
    }
}
