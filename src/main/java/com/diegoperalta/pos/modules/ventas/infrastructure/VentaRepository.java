package com.diegoperalta.pos.modules.ventas.infrastructure;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.ventas.application.dto.ProductoTopDTO;
import com.diegoperalta.pos.modules.ventas.domain.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
        // JPQL: Suma el totalVenta de todas las ventas que pertenezcan a la sesión X
        // COALESCE(..., 0) sirve para que si no hay ventas, devuelva 0 en vez de null
        @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v WHERE v.sesionCaja = :sesion")
        BigDecimal sumarVentasPorSesion(SesionCaja sesion);

        @Query("SELECT v FROM Venta v " +
                        "JOIN FETCH v.usuario " +
                        "LEFT JOIN FETCH v.cliente " +
                        "WHERE v.fecha BETWEEN :inicio AND :fin " +
                        "AND v.estado = 'COMPLETADA'")
        List<Venta> buscarVentasEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

        @Query("SELECT v FROM Venta v " +
                        "JOIN FETCH v.usuario " +
                        "LEFT JOIN FETCH v.cliente " +
                        "JOIN FETCH v.detalles d " +
                        "JOIN FETCH d.producto " +
                        "WHERE v.id = :id")
        Optional<Venta> findByIdConDetalles(@Param("id") Long id);

        @Query("SELECT new com.diegoperalta.pos.modules.ventas.application.dto.ProductoTopDTO(" +
                        "d.producto.nombre, SUM(d.cantidad), SUM(d.subtotal)) " +
                        "FROM DetalleVenta d " +
                        "WHERE d.venta.fecha BETWEEN :inicio AND :fin " +
                        "AND d.venta.estado = 'COMPLETADA' " +
                        "GROUP BY d.producto.nombre " +
                        "ORDER BY SUM(d.subtotal) DESC")
        List<ProductoTopDTO> encontrarTopProductos(
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin,
                        Pageable pageable);
}
