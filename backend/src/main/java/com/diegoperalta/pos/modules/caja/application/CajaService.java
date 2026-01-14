package com.diegoperalta.pos.modules.caja.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;

import com.diegoperalta.pos.modules.caja.application.dto.AperturaCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.CierreCajaDTO;
import com.diegoperalta.pos.modules.caja.application.dto.CorteXDTO;
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

    @Autowired
    private UserProvider userProvider;

    @Value("${app.business.time-zone}")
    private String businessTimeZone;

    @Transactional
    public SesionCaja abrirCaja(AperturaCajaDTO dto) {
        // 1. Obtener usuario actual
        Usuario usuario = obtenerUsuarioActual();

        // 2. VALIDACIÓN: ¿Ya tiene una caja abierta?
        if (sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA").isPresent()) {
            throw new BusinessException("El usuario ya tiene una sesion de caja abierta. Debe cerrar la caja actual",
                    HttpStatus.CONFLICT);
        }

        // 3. Crear la sesión
        SesionCaja sesion = new SesionCaja();
        sesion.setUsuario(usuario);
        sesion.setSaldoInicial(dto.getSaldoInicial());
        sesion.setEstado("ABIERTA");
        sesion.setFechaApertura(Instant.now());

        return sesionCajaRepository.save(sesion);
    }

    @Transactional
    public SesionCaja cerrarCaja(CierreCajaDTO dto) {
        Usuario usuario = obtenerUsuarioActual(); // El que está intentando cerrar

        // 1. Buscar la caja abierta del usuario
        SesionCaja sesion = sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .orElseThrow(() -> new BusinessException("No hay sesion abierta para cerrar", HttpStatus.BAD_REQUEST));

        // VALIDACIÓN DE SEGURIDAD
        if (!sesion.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("No puedes cerrar una caja que no te pertenece", HttpStatus.FORBIDDEN);
        }

        // 2. Validar que esté abierta
        if (!"ABIERTA".equals(sesion.getEstado())) {
            throw new BusinessException("Esta sesión ya está cerrada.", HttpStatus.BAD_REQUEST);
        }

        // 3. Calcular cuánto DEBERÍA haber (Lógica del Sistema)
        BigDecimal ventasEfectivo = ventaRepository.sumarVentasEfectivo(sesion);
        BigDecimal totalIngresos = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "INGRESO");
        BigDecimal totalRetiros = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "RETIRO");

        BigDecimal saldoEsperado = sesion.getSaldoInicial()
                .add(ventasEfectivo)
                .add(totalIngresos)
                .subtract(totalRetiros);

        // 4. Calcular Diferencia (Sobrante o Faltante)
        // Diferencia = Lo que hay fisicamente - Lo que dice el sistema
        BigDecimal diferencia = dto.getSaldoFinalReal().subtract(saldoEsperado);

        // 5. Actualizar y Cerrar
        sesion.setSaldoFinalCalculado(saldoEsperado);
        sesion.setSaldoFinalReal(dto.getSaldoFinalReal());
        sesion.setDiferencia(diferencia);
        sesion.setFechaCierre(Instant.now());
        sesion.setEstado("CERRADA");

        return sesionCajaRepository.save(sesion);
    }

    @Transactional(readOnly = true)
    public CorteXDTO generarCorteX() {
        Usuario usuario = obtenerUsuarioActual();
        SesionCaja sesion = sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .orElseThrow(() -> new BusinessException("No hay sesión abierta para generar corte.",
                        HttpStatus.BAD_REQUEST));

        BigDecimal ventasEfectivo = ventaRepository.sumarVentasEfectivo(sesion);
        BigDecimal ventasOtrosMetodos = ventaRepository.sumarVentasOtrosMetodos(sesion);
        BigDecimal ingresos = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "INGRESO");
        BigDecimal retiros = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "RETIRO");

        BigDecimal saldoEsperado = sesion.getSaldoInicial()
                .add(ventasEfectivo)
                .add(ingresos)
                .subtract(retiros);

        CorteXDTO corteX = new CorteXDTO();
        corteX.setSaldoInicial(sesion.getSaldoInicial());
        corteX.setVentasEfectivo(ventasEfectivo);
        corteX.setVentasOtrosMetodos(ventasOtrosMetodos);
        corteX.setTotalIngresos(ingresos);
        corteX.setTotalRetiros(retiros);
        corteX.setSaldoEsperadoEnCaja(saldoEsperado);
        return corteX;
    }

    @Transactional
    public MovimientoCaja registrarMovimiento(NuevoMovimientoCajaDTO dto) {
        Usuario usuario = obtenerUsuarioActual();

        SesionCaja sesion = sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .orElseThrow(() -> new BusinessException("No hay sesión abierta.", HttpStatus.BAD_REQUEST));

        if (dto.getTipo().equals("RETIRO")) {
            BigDecimal ventasEfectivo = ventaRepository.sumarVentasEfectivo(sesion);
            BigDecimal ingresos = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "INGRESO");
            BigDecimal retiros = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "RETIRO");

            BigDecimal saldoDisponible = sesion.getSaldoInicial()
                    .add(ventasEfectivo)
                    .add(ingresos)
                    .subtract(retiros);

            if (saldoDisponible.compareTo(dto.getMonto()) < 0) {
                throw new BusinessException(
                        "Saldo insuficiente en caja para realizar este retiro. Disponible: $" + saldoDisponible,
                        HttpStatus.BAD_REQUEST);
            }
        }

        MovimientoCaja mov = new MovimientoCaja();
        mov.setSesionCaja(sesion);
        mov.setUsuario(usuario); // Ahora sí existe la columna en BD
        mov.setMonto(dto.getMonto());
        mov.setTipo(dto.getTipo());
        mov.setMotivo(dto.getMotivo()); // Asignamos el motivo

        return movimientoCajaRepository.save(mov);
    }

    public Optional<SesionCaja> obtenerSesionActual() {
        Usuario usuario = obtenerUsuarioActual();
        return sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA");
    }

    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerMovimientosSesionActual() {
        Usuario usuario = obtenerUsuarioActual();
        SesionCaja sesion = sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .orElseThrow(() -> new BusinessException("No hay sesión abierta.", HttpStatus.BAD_REQUEST));

        return movimientoCajaRepository.listarPorSesionConUsuario(sesion);
    }

    @Transactional(readOnly = true)
    public String generarTicketCorteZ(Long sesionId) {
        SesionCaja sesion = sesionCajaRepository.findById(sesionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada"));

        // Recalculamos los totales históricos para mostrarlos en el papel
        BigDecimal ventasEfectivo = ventaRepository.sumarVentasEfectivo(sesion);
        BigDecimal ventasOtros = ventaRepository.sumarVentasOtrosMetodos(sesion);
        BigDecimal ingresos = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "INGRESO");
        BigDecimal retiros = movimientoCajaRepository.sumarPorSesionYTipo(sesion, "RETIRO");

        // Construcción del Ticket
        StringBuilder sb = new StringBuilder();
        String lineaDiv = "--------------------------------\n";
        // Formatter para el ticket (usaremos zona del sistema para imprimir)
        DateTimeFormatter fechaFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.of(businessTimeZone));

        // 1. Cabecera
        centrarTexto(sb, "CORTE DE CAJA (Z)");
        sb.append(lineaDiv);
        sb.append("Cajero: ").append(sesion.getUsuario().getUsername()).append("\n");
        sb.append("Inicio: ").append(fechaFmt.format(sesion.getFechaApertura())).append("\n");
        if (sesion.getFechaCierre() != null) {
            sb.append("Fin:    ").append(fechaFmt.format(sesion.getFechaCierre())).append("\n");
        }
        sb.append(lineaDiv);

        // 2. Balance de Efectivo
        alinearDerecha(sb, "Saldo Inicial: $" + sesion.getSaldoInicial());
        alinearDerecha(sb, "(+) Ventas Efec: $" + ventasEfectivo);
        alinearDerecha(sb, "(+) Ingresos:    $" + ingresos);
        alinearDerecha(sb, "(-) Retiros:     $" + retiros);
        sb.append(lineaDiv);
        alinearDerecha(sb, "(=) ESPERADO:    $" + sesion.getSaldoFinalCalculado());
        alinearDerecha(sb, "    REAL:        $" + sesion.getSaldoFinalReal());
        sb.append(lineaDiv);

        // 3. Diferencia
        BigDecimal diferencia = sesion.getDiferencia();
        String etiquetaDif = diferencia.compareTo(BigDecimal.ZERO) >= 0 ? "SOBRANTE" : "FALTANTE";
        alinearDerecha(sb, etiquetaDif + ": $" + diferencia);

        // 4. Otros Métodos
        sb.append("\n");
        centrarTexto(sb, "OTROS METODOS DE PAGO");
        sb.append(lineaDiv);
        alinearDerecha(sb, "Tarj/Transf:     $" + ventasOtros);

        sb.append("\n\n\n");

        return sb.toString();
    }

    // Métodos auxiliares de formato (Copiados de TicketService para autonomía del
    // módulo)
    private void centrarTexto(StringBuilder sb, String texto) {
        int ancho = 32;
        int espacios = (ancho - texto.length()) / 2;
        if (espacios < 0)
            espacios = 0;
        for (int i = 0; i < espacios; i++)
            sb.append(" ");
        sb.append(texto).append("\n");
    }

    private void alinearDerecha(StringBuilder sb, String texto) {
        int ancho = 32;
        int espacios = ancho - texto.length();
        if (espacios < 0)
            espacios = 0;
        for (int i = 0; i < espacios; i++)
            sb.append(" ");
        sb.append(texto).append("\n");
    }

    private Usuario obtenerUsuarioActual() {
        String username = userProvider.getCurrentUser();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en la sesión actual"));
    }
}
