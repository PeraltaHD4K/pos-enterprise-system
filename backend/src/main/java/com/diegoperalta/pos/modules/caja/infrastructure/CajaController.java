package com.diegoperalta.pos.modules.caja.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.caja.application.CajaService;
import com.diegoperalta.pos.modules.caja.application.dto.AperturaCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.CierreCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.CorteXDTO;
import com.diegoperalta.pos.modules.caja.application.dto.NuevoMovimientoCajaDTO;
import com.diegoperalta.pos.modules.caja.domain.MovimientoCaja;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/caja")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
public class CajaController {
    @Autowired
    private CajaService cajaService;

    @PostMapping("/abrir")
    public ResponseEntity<SesionCaja> abrirCaja(@Valid @RequestBody AperturaCajaDTO dto) {
        SesionCaja sesion = cajaService.abrirCaja(dto);
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/cerrar")
    public ResponseEntity<SesionCaja> cerrarCaja(@Valid @RequestBody CierreCajaDTO dto) {
        SesionCaja sesion = cajaService.cerrarCaja(dto);
        return ResponseEntity.ok(sesion);
    }

    @GetMapping("/corte-x")
    public ResponseEntity<CorteXDTO> obtenerCorteX() {
        return ResponseEntity.ok(cajaService.generarCorteX());
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoCaja>> listarMovimientos() {
        return ResponseEntity.ok(cajaService.obtenerMovimientosSesionActual());
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoCaja> registrarMovimiento(@Valid @RequestBody NuevoMovimientoCajaDTO dto) {
        return ResponseEntity.ok(cajaService.registrarMovimiento(dto));
    }

    @GetMapping("/estado")
    public ResponseEntity<SesionCaja> obtenerEstadoCaja() {
        Optional<SesionCaja> sesion = cajaService.obtenerSesionActual();
        return sesion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/ticket-cierre/{id}")
    public ResponseEntity<String> obtenerTicketCierre(@PathVariable Long id) {
        String ticket = cajaService.generarTicketCorteZ(id);

        return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body(ticket);
    }
}
