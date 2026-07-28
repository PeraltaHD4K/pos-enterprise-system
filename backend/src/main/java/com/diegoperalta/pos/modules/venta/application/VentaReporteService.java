package com.diegoperalta.pos.modules.venta.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.venta.application.dto.ProductoTopDTO;
import com.diegoperalta.pos.modules.venta.application.dto.PuntoGraficaDTO;
import com.diegoperalta.pos.modules.venta.application.dto.ReporteGananciasDTO;
import com.diegoperalta.pos.modules.venta.application.dto.TotalesReporteDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaResumenDTO;
import com.diegoperalta.pos.modules.venta.domain.DetalleVenta;
import com.diegoperalta.pos.modules.venta.domain.Venta;
import com.diegoperalta.pos.modules.venta.infrastructure.VentaRepository;
import com.diegoperalta.pos.modules.cliente.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaReporteService {

    
    private final VentaRepository ventaRepository;
    
    private final ClienteRepository clienteRepository;
    
    private final UsuarioRepository usuarioRepository;

    @Value("${app.business.time-zone}")
    private String businessTimeZone;

    private ZoneId getBusinessZoneId() {
        return ZoneId.of(businessTimeZone);
    }

    @Transactional(readOnly = true)
    public Page<VentaResumenDTO> listarVentas(Pageable pageable) {
        Page<Venta> paginaVentas = ventaRepository.findAllConRelaciones(pageable);
        return paginaVentas.map(this::mapToVentaResumenDTO);
    }

    @Transactional(readOnly = true)
    public ReporteGananciasDTO generarReporteGanancias(Instant inicio, Instant fin) {
        TotalesReporteDTO totales = ventaRepository.sumarReporteGlobal(inicio, fin);
        
        BigDecimal totalVenta = totales != null && totales.getTotalVenta() != null ? totales.getTotalVenta() : BigDecimal.ZERO;
        BigDecimal totalCosto = totales != null && totales.getTotalCosto() != null ? totales.getTotalCosto() : BigDecimal.ZERO;
        long transacciones = totales != null && totales.getTotalTransacciones() != null ? totales.getTotalTransacciones() : 0L;

        ZoneId zoneId = getBusinessZoneId();
        boolean esUnSoloDia = inicio.atZone(zoneId).toLocalDate().isEqual(fin.atZone(zoneId).toLocalDate());
        
        String formato = esUnSoloDia ? "HH24:00" : "YYYY-MM-DD";
        List<Object[]> resultadosGrafica = ventaRepository.agruparVentasPorTiempo(inicio, fin, formato, zoneId.getId());
        
        List<PuntoGraficaDTO> datosGrafica = new ArrayList<>();
        for (Object[] fila : resultadosGrafica) {
            String etiqueta = (String) fila[0];
            Long transaccionesGrupo = ((Number) fila[1]).longValue();
            BigDecimal sumaVenta = fila[2] != null ? new BigDecimal(fila[2].toString()) : BigDecimal.ZERO;
            BigDecimal sumaCosto = fila[3] != null ? new BigDecimal(fila[3].toString()) : BigDecimal.ZERO;
            
            BigDecimal gananciaGrupo = sumaVenta.subtract(sumaCosto);
            datosGrafica.add(new PuntoGraficaDTO(etiqueta, sumaVenta, gananciaGrupo, transaccionesGrupo.intValue()));
        }

        ReporteGananciasDTO reporte = new ReporteGananciasDTO();
        reporte.setTotalVentas(totalVenta);
        reporte.setCostoVentas(totalCosto);
        reporte.setGananciaBruta(totalVenta.subtract(totalCosto));
        reporte.setTotalTransacciones((int) transacciones);
        reporte.setGrafica(datosGrafica);

        if (totalVenta.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = reporte.getGananciaBruta()
                    .divide(totalVenta, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
            reporte.setMargenPorcentaje(margen);
        } else {
            reporte.setMargenPorcentaje(BigDecimal.ZERO);
        }

        if (reporte.getTotalTransacciones() != null && reporte.getTotalTransacciones() > 0) {
            BigDecimal promedio = reporte.getTotalVentas()
                    .divide(new BigDecimal(reporte.getTotalTransacciones()), 2, RoundingMode.HALF_UP);
            reporte.setTicketPromedio(promedio);
        } else {
            reporte.setTicketPromedio(BigDecimal.ZERO);
        }

        return reporte;
    }

    @Transactional(readOnly = true)
    public List<ProductoTopDTO> obtenerTopProductos(Instant inicio, Instant fin, int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return ventaRepository.encontrarTopProductos(inicio, fin, pageable);
    }

    public VentaResumenDTO mapToVentaResumenDTO(Venta venta) {
        VentaResumenDTO dto = new VentaResumenDTO();
        dto.setId(venta.getId());
        dto.setFolio(venta.getFolio());
        dto.setFecha(venta.getFecha());
        dto.setTotalVenta(venta.getTotalVenta());
        dto.setEstado(venta.getEstado());
        if (venta.getClienteId() != null) {
            clienteRepository.findById(venta.getClienteId())
                    .ifPresent(c -> dto.setNombreCliente(c.getNombre()));
        }
        if (venta.getUsuarioId() != null) {
            usuarioRepository.findById(venta.getUsuarioId())
                    .ifPresent(u -> dto.setNombreVendedor(u.getUsername()));
        }
        return dto;
    }
}
