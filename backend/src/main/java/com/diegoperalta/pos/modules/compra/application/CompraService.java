package com.diegoperalta.pos.modules.compra.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.compra.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compra.application.dto.ItemCompraDTO;
import com.diegoperalta.pos.modules.compra.domain.Compra;
import com.diegoperalta.pos.modules.compra.domain.DetalleCompra;
import com.diegoperalta.pos.modules.compra.domain.Proveedor;
import com.diegoperalta.pos.modules.compra.infrastructure.CompraRepository;
import com.diegoperalta.pos.modules.compra.infrastructure.ProveedorRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompraService {
    
    private final CompraRepository compraRepository;
    
    private final ProveedorRepository proveedorRepository;
    
    private final UsuarioRepository usuarioRepository;
    
    private final ProductoRepository productoRepository;
    
    private final ProductoService productoService;
    
    private final CurrentUserProvider userProvider;

    @Transactional
    public Compra registrarCompra(CompraRegistroDTO dto) {
        // Validaciones básicas
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("La compra debe incluir al menos un producto", HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = userProvider.getCurrentUserDetails();

        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + dto.getProveedorId()));

        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setUsuarioId(usuario.getId());
        compra.setFolioFactura(dto.getFolioFactura());
        compra.setObservaciones(dto.getObservaciones());

        compra.setFechaPedido(Instant.now());
        compra.setFechaEstimadaEntrega(dto.getFechaEstimadaEntrega());

        String estadoInicial = (dto.getEstado() != null) ? dto.getEstado() : "COMPLETADA";
        compra.setEstado(estadoInicial);

        if ("COMPLETADA".equals(estadoInicial)) {
            compra.setFechaRecepcion(Instant.now());
        }

        compra.setTotal(BigDecimal.ZERO);

        compra = compraRepository.save(compra);

        BigDecimal totalCompra = BigDecimal.ZERO;
        compra.setDetalles(new ArrayList<>());

        List<UUID> productoIds = dto.getItems().stream().map(ItemCompraDTO::getProductoId).toList();
        List<Producto> productosEncontrados = productoRepository.findAllById(productoIds);
        java.util.Map<UUID, Producto> productosMap = productosEncontrados.stream().collect(java.util.stream.Collectors.toMap(Producto::getId, p -> p));
        List<ProductoService.EntradaCompraInfo> entradasBatch = new java.util.ArrayList<>();

        for (ItemCompraDTO item : dto.getItems()) {
            Producto producto = productosMap.get(item.getProductoId());
            if (producto == null) {
                throw new ResourceNotFoundException("Producto no encontrado ID: " + item.getProductoId());
            }

            // 1. Crear Detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProductoId(producto.getId());

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

                    entradasBatch.add(new ProductoService.EntradaCompraInfo(producto, totalPiezas.intValue(), costoUnitario));
                }
            }
            compra.getDetalles().add(detalle);
        }

        compra.setTotal(totalCompra);
        Compra compraGuardada = compraRepository.save(compra);
        if (!entradasBatch.isEmpty()) {
            productoService.registrarEntradasPorCompraBatch(entradasBatch, compraGuardada.getId(), usuario);
        }
        return compraGuardada;
    }

    @Transactional
    public Compra confirmarRecepcion(UUID compraId) {
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
        List<ProductoService.EntradaCompraInfo> entradasBatch = new java.util.ArrayList<>();

        for (DetalleCompra detalle : compra.getDetalles()) {
            detalle.setCantidadRecibida(detalle.getCantidadPedida());

            if (detalle.getCostoTotalRenglon() != null) {
                BigDecimal totalPiezas = new BigDecimal(detalle.getTotalPiezasReales());
                if (totalPiezas.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal unitario = detalle.getCostoTotalRenglon().divide(totalPiezas, 4, RoundingMode.HALF_UP);
                    detalle.setCostoUnitarioCalculado(unitario);
                    Producto p = productoRepository.findById(detalle.getProductoId()).orElseThrow();
                    entradasBatch.add(new ProductoService.EntradaCompraInfo(p, totalPiezas.intValue(), unitario));
                }
                totalReal = totalReal.add(detalle.getCostoTotalRenglon());
            }
        }

        if (!entradasBatch.isEmpty()) {
            productoService.registrarEntradasPorCompraBatch(entradasBatch, compra.getId(), userProvider.getCurrentUserDetails());
        }

        compra.setEstado("COMPLETADA");
        compra.setFechaRecepcion(Instant.now());
        compra.setTotal(totalReal);

        return compraRepository.save(compra);
    }
}
