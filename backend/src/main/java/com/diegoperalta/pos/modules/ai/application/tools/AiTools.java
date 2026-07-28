package com.diegoperalta.pos.modules.ai.application.tools;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.diegoperalta.pos.modules.venta.application.VentaReporteService;
import com.diegoperalta.pos.modules.venta.application.dto.ReporteGananciasDTO;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiTools {

    private final VentaReporteService ventaReporteService;
    private final ProductoRepository productoRepository;
    private final SesionCajaRepository sesionCajaRepository;

    public record ConsultarVentasRequest(String fechaEspecifica) {}
    public record ConsultarVentasResponse(BigDecimal totalVentas, int transacciones, BigDecimal gananciaBruta) {}

    @Tool(description = "Obtiene el total de ventas, ingresos, ganancias y numero de transacciones del dia actual en el sistema de Punto de Venta (POS). No requiere parametros obligatorios.")
    public ConsultarVentasResponse consultarVentasDelDia(ConsultarVentasRequest request) {
        ZoneId zoneId = ZoneId.of("America/Mexico_City");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        Instant inicio = now.truncatedTo(ChronoUnit.DAYS).toInstant();
        Instant fin = now.truncatedTo(ChronoUnit.DAYS).plusDays(1).toInstant();

        ReporteGananciasDTO reporte = ventaReporteService.generarReporteGanancias(inicio, fin);

        return new ConsultarVentasResponse(
            reporte.getTotalVentas(),
            reporte.getTotalTransacciones(),
            reporte.getGananciaBruta()
        );
    }

    public record ConsultarInventarioRequest() {}
    public record ProductoBajoStockDTO(String nombre, String sku, int stockActual, int stockMinimo) {}
    public record ConsultarInventarioResponse(List<ProductoBajoStockDTO> productosFaltantes) {}

    @Tool(description = "Busca y devuelve una lista de todos los productos en el inventario que tienen stock igual o menor a su limite minimo permitido. Sirve para saber que productos se necesitan resurtir.")
    public ConsultarInventarioResponse consultarInventarioBajo(ConsultarInventarioRequest request) {
        List<Producto> productosBajos = productoRepository.encontrarProductosConStockBajo();
        List<ProductoBajoStockDTO> dtos = productosBajos.stream()
            .map(p -> new ProductoBajoStockDTO(
                p.getNombre(),
                p.getSku(),
                p.getStockActual() != null ? p.getStockActual() : 0,
                p.getStockMinimo() != null ? p.getStockMinimo() : 0
            ))
            .collect(Collectors.toList());

        return new ConsultarInventarioResponse(dtos);
    }

    public record ConsultarEstadoCajaRequest() {}
    public record ConsultarEstadoCajaResponse(int sesionesAbiertas, BigDecimal sumaSaldoInicial) {}

    @Tool(description = "Consulta el estado global de las cajas del sistema. Devuelve cuantas sesiones de caja estan actualmente abiertas por los cajeros y la suma del saldo inicial con el que abrieron.")
    public ConsultarEstadoCajaResponse consultarEstadoCajaGlobal(ConsultarEstadoCajaRequest request) {
        List<SesionCaja> cajasAbiertas = sesionCajaRepository.findAll().stream()
            .filter(c -> "ABIERTA".equals(c.getEstado()))
            .collect(Collectors.toList());

        BigDecimal saldoSuma = BigDecimal.ZERO;
        for (SesionCaja caja : cajasAbiertas) {
            if (caja.getSaldoInicial() != null) {
                saldoSuma = saldoSuma.add(caja.getSaldoInicial());
            }
        }

        return new ConsultarEstadoCajaResponse(cajasAbiertas.size(), saldoSuma);
    }
}
