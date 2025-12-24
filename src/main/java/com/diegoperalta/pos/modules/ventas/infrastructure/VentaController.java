package com.diegoperalta.pos.modules.ventas.infrastructure;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.ventas.application.VentaService;
import com.diegoperalta.pos.modules.ventas.application.dto.ReporteGananciasDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.VentaResumenDTO;
import com.diegoperalta.pos.modules.ventas.domain.Venta;

@RestController
@RequestMapping("/ventas")
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody VentaRegistroDTO dto) {
        Venta nuevaVenta = ventaService.registrarVenta(dto);
        return ResponseEntity.ok(nuevaVenta);
    }

    @GetMapping
    public ResponseEntity<Page<VentaResumenDTO>> listarVentas(
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        Page<VentaResumenDTO> ventas = ventaService.listarVentas(pageable);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/reporte/ganancias")
    public ResponseEntity<ReporteGananciasDTO> obtenerReporteGanancias(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        LocalDate inicio = (fechaInicio != null) ? fechaInicio : LocalDate.now();
        LocalDate fin = (fechaFin != null) ? fechaFin : LocalDate.now();

        LocalDateTime start = inicio.atStartOfDay();
        LocalDateTime end = fin.atTime(LocalTime.MAX);

        return ResponseEntity.ok(ventaService.generarReporteGanancias(start, end));
    }
}
