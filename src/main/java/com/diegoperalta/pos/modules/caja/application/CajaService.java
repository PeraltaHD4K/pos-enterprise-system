package com.diegoperalta.pos.modules.caja.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.caja.application.dto.AperturaCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.CierreCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.NuevoMovimientoCajaDTO;
import com.diegoperalta.pos.modules.caja.domain.MovimientoCaja;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.MovimientoCajaRepository;
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

    @Autowired
    private MovimientoCajaRepository movimientoCajaRepository;

    @Transactional
    public SesionCaja abrirCaja(AperturaCajaDTO dto) {
        // 1. Obtener usuario actual (Hardcodeado ID 1 por ahora)
        Usuario usuario = obtenerUsuarioActual();

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
        BigDecimal totalIngresos = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "INGRESO");
        BigDecimal totalRetiros = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "RETIRO");

        BigDecimal saldoEsperado = sesion.getSaldoInicial()
                .add(totalVentas)
                .add(totalIngresos)
                .subtract(totalRetiros);

        // 4. Calcular Diferencia (Sobrante o Faltante)
        // Diferencia = Lo que hay fisicamente - Lo que dice el sistema
        BigDecimal diferencia = dto.getSaldoFinalReal().subtract(saldoEsperado);

        // 5. Actualizar y Cerrar
        sesion.setSaldoFinalCalculado(saldoEsperado);
        sesion.setSaldoFinalReal(dto.getSaldoFinalReal());
        sesion.setDiferencia(diferencia);
        sesion.setFechaCierre(LocalDateTime.now());
        sesion.setEstado("CERRADA");

        return sesionCajaRepository.save(sesion);
    }

    @Transactional
    public MovimientoCaja registrarMovimiento(NuevoMovimientoCajaDTO dto) {
        Usuario usuario = obtenerUsuarioActual();

        SesionCaja sesion = sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .orElseThrow(() -> new RuntimeException("No hay sesión abierta."));

        MovimientoCaja mov = new MovimientoCaja();
        mov.setSesionCaja(sesion);
        mov.setUsuario(usuario); // Ahora sí existe la columna en BD
        mov.setMonto(dto.getMonto());
        mov.setTipo(dto.getTipo());
        mov.setMotivo(dto.getMotivo()); // Asignamos el motivo

        return movimientoCajaRepository.save(mov);
    }

    private Usuario obtenerUsuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la sesión actual"));
    }
}
