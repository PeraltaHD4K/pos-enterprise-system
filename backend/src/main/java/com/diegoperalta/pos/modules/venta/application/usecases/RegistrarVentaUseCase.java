package com.diegoperalta.pos.modules.venta.application.usecases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.cliente.domain.Cliente;
import com.diegoperalta.pos.modules.cliente.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import com.diegoperalta.pos.modules.venta.application.dto.ItemVentaDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaResponseDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaItemResponseDTO;
import com.diegoperalta.pos.modules.venta.domain.DetalleVenta;
import com.diegoperalta.pos.modules.venta.domain.Venta;
import com.diegoperalta.pos.modules.venta.domain.events.VentaCompletadaEvent;
import com.diegoperalta.pos.modules.venta.infrastructure.VentaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrarVentaUseCase {


    private final VentaRepository ventaRepository;

    private final ProductoRepository productoRepository;

    private final ProductoService productoService;

    private final ClienteRepository clienteRepository;

    private final SesionCajaRepository sesionCajaRepository;

    private final CurrentUserProvider userProvider;

    private final ApplicationEventPublisher eventPublisher;

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public VentaResponseDTO ejecutar(VentaRegistroDTO dto) {
        Usuario usuario = userProvider.getCurrentUserDetails();

        SesionCaja sesion = sesionCajaRepository.findByUsuarioIdAndEstado(usuario.getId(), "ABIERTA")
                .orElseThrow(() -> new BusinessException("No hay sesión de caja abierta. Abra caja primero.", HttpStatus.BAD_REQUEST));

        UUID idClienteParaBuscar = dto.getClienteId();
        Cliente cliente = null;
        if (idClienteParaBuscar != null) {
            cliente = clienteRepository.findById(idClienteParaBuscar)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + idClienteParaBuscar));
        }

        Venta venta = new Venta();
        venta.setSesionCaja(sesion);
        venta.setClienteId(cliente != null ? cliente.getId() : null);
        venta.setUsuarioId(usuario.getId());
        venta.setMetodoPago(dto.getMetodoPago());
        venta.setFolio(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        venta.setDetalles(new ArrayList<>());
        venta.setEstado("COMPLETADA");
        venta.setTotalVenta(BigDecimal.ZERO);
        venta.setMontoPagado(dto.getMontoPagado());
        venta.setCambio(BigDecimal.ZERO);

        venta = ventaRepository.save(venta);
        BigDecimal totalAcumulado = BigDecimal.ZERO;

        List<UUID> productoIds = dto.getItems().stream().map(ItemVentaDTO::getProductoId).toList();
        List<Producto> productosEncontrados = productoRepository.findAllById(productoIds);
        if (productosEncontrados.size() != productoIds.size()) {
            List<UUID> encontradosIds = productosEncontrados.stream().map(Producto::getId).toList();
            List<UUID> faltantes = productoIds.stream().filter(id -> !encontradosIds.contains(id)).toList();
            throw new ResourceNotFoundException("Productos no encontrados con IDs: " + faltantes);
        }
        Map<UUID, Producto> productosMap = productosEncontrados.stream().collect(Collectors.toMap(Producto::getId, p -> p));

        List<ProductoService.SalidaVentaInfo> salidasBatch = new java.util.ArrayList<>();

        for (ItemVentaDTO item : dto.getItems()) {
            Producto producto = productosMap.get(item.getProductoId());
            salidasBatch.add(new ProductoService.SalidaVentaInfo(producto, item.getCantidad()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProductoId(producto.getId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());

            BigDecimal costoSnapshot = producto.getCostoPromedio() != null ? producto.getCostoPromedio() : BigDecimal.ZERO;
            detalle.setCostoUnitarioSnapshot(costoSnapshot);

            BigDecimal subtotal = producto.getPrecioVenta().multiply(new BigDecimal(item.getCantidad()));
            detalle.setSubtotal(subtotal);

            venta.getDetalles().add(detalle);
            totalAcumulado = totalAcumulado.add(subtotal);
        }

        BigDecimal montoPagado = dto.getMontoPagado();
        if (montoPagado.compareTo(totalAcumulado) < 0) {
            throw new BusinessException(String.format("Pago insuficiente. Total: $%s, Pagado $%s", totalAcumulado, montoPagado), HttpStatus.BAD_REQUEST);
        }
        BigDecimal cambio = montoPagado.subtract(totalAcumulado);

        venta.setTotalVenta(totalAcumulado);
        venta.setMontoPagado(montoPagado);
        venta.setCambio(cambio);

        Venta ventaGuardada = ventaRepository.save(venta);

        productoService.registrarSalidasPorVentaBatch(salidasBatch, ventaGuardada.getId(), usuario);

        eventPublisher.publishEvent(new VentaCompletadaEvent(
                ventaGuardada.getId(), ventaGuardada.getFolio(), ventaGuardada.getTotalVenta(), ventaGuardada.getFecha()));

        return mapToVentaResponseDTO(ventaGuardada);
    }

    private VentaResponseDTO mapToVentaResponseDTO(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setFolio(venta.getFolio());
        dto.setFecha(venta.getFecha());
        dto.setEstado(venta.getEstado());
        dto.setMetodoPago(venta.getMetodoPago());
        dto.setTotalVenta(venta.getTotalVenta());
        dto.setMontoPagado(venta.getMontoPagado());
        dto.setCambio(venta.getCambio());
        if (venta.getClienteId() != null) {
            clienteRepository.findById(venta.getClienteId())
                    .ifPresent(c -> dto.setNombreCliente(c.getNombre()));
        }
        if (venta.getUsuarioId() != null) {
            usuarioRepository.findById(venta.getUsuarioId())
                    .ifPresent(u -> dto.setNombreVendedor(u.getUsername()));
        }
        List<VentaItemResponseDTO> items = venta.getDetalles().stream().map(d -> {
            VentaItemResponseDTO item = new VentaItemResponseDTO();
            item.setProductoId(d.getProductoId());
            productoRepository.findById(d.getProductoId())
                    .ifPresent(p -> item.setNombreProducto(p.getNombre()));
            item.setCantidad(d.getCantidad());
            item.setPrecioUnitario(d.getPrecioUnitario());
            item.setSubtotal(d.getSubtotal());
            return item;
        }).toList();
        dto.setDetalles(items);
        return dto;
    }
}
