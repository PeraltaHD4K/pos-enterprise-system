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
import com.diegoperalta.pos.modules.caja.application.dto.MovimientoCajaResponseDTO;
import com.diegoperalta.pos.modules.caja.application.dto.NuevoMovimientoCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.SesionCajaResponseDTO;
import com.diegoperalta.pos.modules.caja.domain.MovimientoCaja;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@RestController
@RequestMapping("/caja")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
@RequiredArgsConstructor
public class CajaController {
    private final CajaService cajaService;

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/abrir")
    public ResponseEntity<SesionCajaResponseDTO> abrirCaja(@Valid @RequestBody AperturaCajaDTO dto) {
        SesionCaja sesion = cajaService.abrirCaja(dto);
        return ResponseEntity.ok(mapToDTO(sesion));
    }

    @PostMapping("/cerrar")
    public ResponseEntity<SesionCajaResponseDTO> cerrarCaja(@Valid @RequestBody CierreCajaDTO dto) {
        SesionCaja sesion = cajaService.cerrarCaja(dto);
        return ResponseEntity.ok(mapToDTO(sesion));
    }

    @GetMapping("/corte-x")
    public ResponseEntity<CorteXDTO> obtenerCorteX() {
        return ResponseEntity.ok(cajaService.generarCorteX());
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoCajaResponseDTO>> listarMovimientos() {
        return ResponseEntity.ok(cajaService.obtenerMovimientosSesionActual().stream().map(this::mapMovimientoToDTO).toList());
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoCajaResponseDTO> registrarMovimiento(@Valid @RequestBody NuevoMovimientoCajaDTO dto) {
        return ResponseEntity.ok(mapMovimientoToDTO(cajaService.registrarMovimiento(dto)));
    }

    @GetMapping("/estado")
    public ResponseEntity<SesionCajaResponseDTO> obtenerEstadoCaja() {
        Optional<SesionCaja> sesion = cajaService.obtenerSesionActual();
        return sesion.map(this::mapToDTO).map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/ticket-cierre/{id}")
    public ResponseEntity<String> obtenerTicketCierre(@PathVariable UUID id) {
        String ticket = cajaService.generarTicketCorteZ(id);

        return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body(ticket);
    }

    private SesionCajaResponseDTO mapToDTO(SesionCaja s) {
        SesionCajaResponseDTO dto = new SesionCajaResponseDTO();
        dto.setId(s.getId());
        if (s.getUsuarioId() != null) {
            usuarioRepository.findById(s.getUsuarioId())
                    .ifPresent(u -> dto.setNombreUsuario(u.getUsername()));
        }
        dto.setFechaApertura(s.getFechaApertura());
        dto.setFechaCierre(s.getFechaCierre());
        dto.setSaldoInicial(s.getSaldoInicial());
        dto.setSaldoFinalCalculado(s.getSaldoFinalCalculado());
        dto.setSaldoFinalReal(s.getSaldoFinalReal());
        dto.setDiferencia(s.getDiferencia());
        dto.setEstado(s.getEstado());
        return dto;
    }

    private MovimientoCajaResponseDTO mapMovimientoToDTO(MovimientoCaja m) {
        String nombreUsuario = m.getUsuarioId() != null
            ? usuarioRepository.findById(m.getUsuarioId()).map(u -> u.getUsername()).orElse(null)
            : null;

        return new MovimientoCajaResponseDTO(
            m.getId(),
            m.getSesionCaja() != null ? m.getSesionCaja().getId() : null,
            nombreUsuario,
            m.getMonto(),
            m.getTipo(),
            m.getMotivo(),
            m.getFecha()
        );
    }
}
