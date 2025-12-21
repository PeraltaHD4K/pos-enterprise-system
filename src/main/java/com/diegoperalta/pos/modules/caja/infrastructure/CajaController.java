package com.diegoperalta.pos.modules.caja.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.caja.application.CajaService;
import com.diegoperalta.pos.modules.caja.application.dto.AperturaCajaDTO;
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
}
