package com.diegoperalta.pos.modules.caja.application;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.caja.application.dto.AperturaCajaDTO;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;

@Service
public class CajaService {
    @Autowired
    private SesionCajaRepository sesionCajaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public SesionCaja abrirCaja(AperturaCajaDTO dto) {
        // 1. Obtener usuario actual (Hardcodeado ID 1 por ahora)
        Usuario usuario = usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. VALIDACIÓN: ¿Ya tiene una caja abierta?
        if (sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA").isPresent()) {
            throw new RuntimeException("El usuario ya tiene una sesion de caja abierta. Debe cerrar la caja actual");
        }

        // 3. Crear la sesión
        SesionCaja sesion = new SesionCaja();
        sesion.setUsuario(usuario);
        sesion.setSaldoInicial(dto.getSaldoInicial());
        sesion.setEstado("ABIERTA");
        sesion.setFechaApertura(LocalDateTime.now());

        return sesionCajaRepository.save(sesion);
    }
}
