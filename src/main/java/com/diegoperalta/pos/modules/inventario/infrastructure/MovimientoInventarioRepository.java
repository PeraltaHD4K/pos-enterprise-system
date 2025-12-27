package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    @Query("SELECT m FROM MovimientoInventario m " +
            "JOIN FETCH m.producto p " +
            "LEFT JOIN FETCH p.categoria " +
            "JOIN FETCH m.usuario " +
            "ORDER BY m.fecha DESC")
    List<MovimientoInventario> findAllConDatos();
}
