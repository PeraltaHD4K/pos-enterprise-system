package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;
import java.util.UUID;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, UUID> {

    @Query("SELECT m FROM MovimientoInventario m " +
            "JOIN FETCH m.producto p " +
            "LEFT JOIN FETCH p.categoria " +
            "ORDER BY m.fecha DESC")
    List<MovimientoInventario> findAllConDatos();

    @Query("SELECT m FROM MovimientoInventario m " +
            "WHERE m.producto.id = :productoId " +
            "ORDER BY m.fecha DESC")
    List<MovimientoInventario> findByProductoId(@Param("productoId") UUID productoId);
}
