package com.diegoperalta.pos.modules.caja.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.caja.application.dto.AperturaCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.CierreCajaDTO;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.ventas.infrastructure.VentaRepository;

@Service
public class CajaService {
    @Autowired
    private SesionCajaRepository sesionCajaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VentaRepository ventaRepository;

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

    @Transactional
    public SesionCaja cerrarCaja(Long sesionId, CierreCajaDTO dto) {
        // 1. Buscar la sesión
        SesionCaja sesion = sesionCajaRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // 2. Validar que esté abierta
        if (!"ABIERTA".equals(sesion.getEstado())) {
            throw new RuntimeException("Esta sesión ya está cerrada.");
        }

        // 3. Calcular cuánto DEBERÍA haber (Lógica del Sistema)
        BigDecimal totalVentas = ventaRepository.sumarVentasPorSesion(sesion);
        BigDecimal saldoEsperado = sesion.getSaldoInicial().add(totalVentas);

        // 4. Calcular Diferencia (Sobrante o Faltante)
        // Diferencia = Lo que hay fisicamente - Lo que dice el sistema
        BigDecimal diferencia = dto.getSaldoFinalReal().subtract(saldoEsperado);

        // 5. Actualizar y Cerrar
        sesion.setSaldoFinalCalculado(saldoEsperado);
        sesion.setSaldoFinalReal(dto.getSaldoFinalReal());
        sesion.setDiferencia(diferencia);
        sesion.setFechaCierre(java.time.LocalDateTime.now());
        sesion.setEstado("CERRADA");

        return sesionCajaRepository.save(sesion);
    }
}
