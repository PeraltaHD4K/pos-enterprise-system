package com.diegoperalta.pos.modules.venta.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.cliente.domain.Cliente;
import com.diegoperalta.pos.modules.cliente.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.application.AutorizacionService;
import com.diegoperalta.pos.modules.iam.application.dto.AutorizacionDTO;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import com.diegoperalta.pos.modules.venta.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaResponseDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaResumenDTO;
import com.diegoperalta.pos.modules.venta.application.usecases.CancelarVentaUseCase;
import com.diegoperalta.pos.modules.venta.application.usecases.RegistrarVentaUseCase;
import com.diegoperalta.pos.modules.venta.domain.Venta;
import com.diegoperalta.pos.modules.venta.infrastructure.VentaRepository;
import com.diegoperalta.pos.modules.venta.domain.events.VentaCompletadaEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    
    private final VentaRepository ventaRepository;
    
    private final ProductoRepository productoRepository;
    
    private final ProductoService productoService;
    
    private final ClienteRepository clienteRepository;
    
    private final UsuarioRepository usuarioRepository;
    
    private final SesionCajaRepository sesionCajaRepository;
    
    private final CurrentUserProvider userProvider;
    
    private final AutorizacionService autorizacionService;
    
    private final RegistrarVentaUseCase registrarVentaUseCase;
    
    private final CancelarVentaUseCase cancelarVentaUseCase;
    
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.business.time-zone}")
    private String businessTimeZone;

    private ZoneId getBusinessZoneId() {
        return ZoneId.of(businessTimeZone);
    }

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRegistroDTO dto) {
        return registrarVentaUseCase.ejecutar(dto);
    }

    @Transactional
    public VentaResponseDTO cancelarVenta(String folio, AutorizacionDTO autorizacion) {
        return cancelarVentaUseCase.ejecutar(folio, autorizacion);
    }

    public Page<VentaResumenDTO> obtenerVentasDelDiaUsuarioActual(Pageable pageable) {
        Usuario usuario = userProvider.getCurrentUserDetails();
        ZoneId zoneId = getBusinessZoneId();
        LocalDate hoyLocal = LocalDate.now(zoneId);
        Instant inicioDia = hoyLocal.atStartOfDay(zoneId).toInstant();
        Instant finDia = hoyLocal.atTime(LocalTime.MAX).atZone(zoneId).toInstant();

        Page<Venta> ventas = ventaRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(
                usuario.getId(), inicioDia, finDia, pageable);

        return ventas.map(venta -> {
            VentaResumenDTO dto = new VentaResumenDTO();
            dto.setId(venta.getId());
            dto.setFolio(venta.getFolio());
            dto.setFecha(venta.getFecha());
            dto.setTotalVenta(venta.getTotalVenta());
            dto.setEstado(venta.getEstado());
            dto.setNombreVendedor(usuario.getUsername());
            if (venta.getClienteId() != null) {
                clienteRepository.findById(venta.getClienteId())
                        .ifPresent(c -> dto.setNombreCliente(c.getNombre()));
            }
            return dto;
        });
    }
}
