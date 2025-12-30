package com.diegoperalta.pos.modules.caja.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.caja.application.CajaService;
import com.diegoperalta.pos.modules.caja.application.dto.AperturaCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.CierreCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.NuevoMovimientoCajaDTO;
import com.diegoperalta.pos.modules.caja.domain.MovimientoCaja;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;

@RestController
@RequestMapping("/caja")
public class CajaController {
    @Autowired
    private CajaService cajaService;

    @PostMapping("/abrir")
    public ResponseEntity<SesionCaja> abrirCaja(@RequestBody AperturaCajaDTO dto) {
        SesionCaja sesion = cajaService.abrirCaja(dto);
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/cerrar")
    public ResponseEntity<SesionCaja> cerrarCaja(@RequestBody CierreCajaDTO dto) {
        SesionCaja sesion = cajaService.cerrarCaja(dto);
        return ResponseEntity.ok(sesion);
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoCaja>> listarMovimientos() {
        return ResponseEntity.ok(cajaService.obtenerMovimientosSesionActual());
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoCaja> registrarMovimiento(@RequestBody NuevoMovimientoCajaDTO dto) {
        return ResponseEntity.ok(cajaService.registrarMovimiento(dto));
    }

    @GetMapping("/estado")
    public ResponseEntity<SesionCaja> obtenerEstadoCaja() {
        Optional<SesionCaja> sesion = cajaService.obtenerSesionActual();
        return sesion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
