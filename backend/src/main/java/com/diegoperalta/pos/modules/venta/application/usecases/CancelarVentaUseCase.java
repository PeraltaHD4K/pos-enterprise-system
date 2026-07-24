package com.diegoperalta.pos.modules.venta.application.usecases;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.iam.application.AutorizacionService;
import com.diegoperalta.pos.modules.iam.application.dto.AutorizacionDTO;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.venta.application.dto.VentaResponseDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaItemResponseDTO;
import com.diegoperalta.pos.modules.venta.domain.DetalleVenta;
import com.diegoperalta.pos.modules.venta.domain.Venta;
import com.diegoperalta.pos.modules.venta.infrastructure.VentaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CancelarVentaUseCase {

    
    private final AutorizacionService autorizacionService;
    
    private final VentaRepository ventaRepository;
    
    private final ProductoService productoService;

    @Transactional
    public VentaResponseDTO ejecutar(String folio, AutorizacionDTO autorizacion) {
        autorizacionService.validarAutorizacion(autorizacion, "ADMIN", "GERENTE");

        Venta venta = ventaRepository.findByFolioConDetalles(folio)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (venta.getEstado().equals("CANCELADA")) {
            throw new BusinessException("La venta ya se encuentra cancelada", HttpStatus.BAD_REQUEST);
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            productoService.devolverStockPorCancelacion(
                    detalle.getProducto().getId(), detalle.getCantidad(), venta.getFolio());
        }
        venta.setEstado("CANCELADA");
        return mapToVentaResponseDTO(ventaRepository.save(venta));
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
        if (venta.getCliente() != null) {
            dto.setNombreCliente(venta.getCliente().getNombre());
        }
        if (venta.getUsuario() != null) {
            dto.setNombreVendedor(venta.getUsuario().getUsername());
        }
        List<VentaItemResponseDTO> items = venta.getDetalles().stream().map(d -> {
            VentaItemResponseDTO item = new VentaItemResponseDTO();
            item.setProductoId(d.getProducto().getId());
            item.setNombreProducto(d.getProducto().getNombre());
            item.setCantidad(d.getCantidad());
            item.setPrecioUnitario(d.getPrecioUnitario());
            item.setSubtotal(d.getSubtotal());
            return item;
        }).toList();
        dto.setDetalles(items);
        return dto;
    }
}
