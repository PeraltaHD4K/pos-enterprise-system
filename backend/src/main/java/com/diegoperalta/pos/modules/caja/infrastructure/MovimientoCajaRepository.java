package com.diegoperalta.pos.modules.caja.infrastructure;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.diegoperalta.pos.modules.caja.domain.MovimientoCaja;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    // Sumar todos los movimientos de cierto tipo en una sesión
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.sesionCaja = :sesion AND m.tipo = :tipo")
    BigDecimal sumarPorSesionYTipo(SesionCaja sesion, String tipo);

    @Query("SELECT m FROM MovimientoCaja m " +
            "JOIN FETCH m.usuario " +
            "WHERE m.sesionCaja = :sesion " +
            "ORDER BY m.fecha DESC")
    List<MovimientoCaja> listarPorSesionConUsuario(@Param("sesion") SesionCaja sesion);
}
