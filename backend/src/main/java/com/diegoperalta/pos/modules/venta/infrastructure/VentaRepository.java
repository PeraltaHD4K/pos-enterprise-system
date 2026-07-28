package com.diegoperalta.pos.modules.venta.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.venta.application.dto.ProductoTopDTO;
import com.diegoperalta.pos.modules.venta.application.dto.TotalesReporteDTO;
import com.diegoperalta.pos.modules.venta.domain.Venta;
import java.util.UUID;

@Repository
public interface VentaRepository extends JpaRepository<Venta, UUID> {
        // JPQL: Suma el totalVenta de todas las ventas que pertenezcan a la sesión X
        // COALESCE(..., 0) sirve para que si no hay ventas, devuelva 0 en vez de null
        @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v WHERE v.sesionCaja = :sesion AND v.estado = 'COMPLETADA'")
        BigDecimal sumarVentasPorSesion(SesionCaja sesion);

        @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v " +
                        "WHERE v.sesionCaja = :sesion " +
                        "AND v.estado = 'COMPLETADA' " +
                        "AND v.metodoPago = 'EFECTIVO'")
        BigDecimal sumarVentasEfectivo(@Param("sesion") SesionCaja sesion);

        @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v " +
                        "WHERE v.sesionCaja = :sesion " +
                        "AND v.estado = 'COMPLETADA' " +
                        "AND v.metodoPago <> 'EFECTIVO'")
        BigDecimal sumarVentasOtrosMetodos(@Param("sesion") SesionCaja sesion);

        @Query("SELECT v FROM Venta v " +
                        "WHERE v.fecha BETWEEN :inicio AND :fin " +
                        "AND v.estado = 'COMPLETADA'")
        List<Venta> buscarVentasEnRango(@Param("inicio") Instant inicio, @Param("fin") Instant fin);

        @Query("SELECT v FROM Venta v " +
                        "LEFT JOIN FETCH v.detalles d " +
                        "WHERE v.id = :id")
        Optional<Venta> findByIdConDetalles(@Param("id") UUID id);

        @Query("SELECT new com.diegoperalta.pos.modules.venta.application.dto.ProductoTopDTO(" +
                        "p.nombre, SUM(d.cantidad), SUM(d.subtotal)) " +
                        "FROM DetalleVenta d " +
                        "JOIN com.diegoperalta.pos.modules.inventario.domain.Producto p ON d.productoId = p.id " +
                        "WHERE d.venta.fecha BETWEEN :inicio AND :fin " +
                        "AND d.venta.estado = 'COMPLETADA' " +
                        "GROUP BY p.nombre " +
                        "ORDER BY SUM(d.subtotal) DESC")
        List<ProductoTopDTO> encontrarTopProductos(
                        @Param("inicio") Instant inicio,
                        @Param("fin") Instant fin,
                        Pageable pageable);

        @Query("SELECT new com.diegoperalta.pos.modules.venta.application.dto.TotalesReporteDTO(" +
                        "COUNT(DISTINCT v.id), COALESCE(SUM(d.subtotal), 0), COALESCE(SUM(d.costoUnitarioSnapshot * d.cantidad), 0)) " +
                        "FROM Venta v JOIN v.detalles d " +
                        "WHERE v.fecha BETWEEN :inicio AND :fin AND v.estado = 'COMPLETADA'")
        TotalesReporteDTO sumarReporteGlobal(@Param("inicio") Instant inicio, @Param("fin") Instant fin);

        @Query(value = "SELECT " +
                        "to_char(v.fecha AT TIME ZONE :timeZone, :formato) as etiqueta, " +
                        "COUNT(DISTINCT v.id) as transacciones, " +
                        "COALESCE(SUM(d.subtotal), 0) as totalVenta, " +
                        "COALESCE(SUM(d.costo_unitario_snapshot * d.cantidad), 0) as totalCosto " +
                        "FROM ventas v " +
                        "JOIN detalle_ventas d ON v.id = d.venta_id " +
                        "WHERE v.fecha BETWEEN :inicio AND :fin AND v.estado = 'COMPLETADA' " +
                        "GROUP BY 1 " +
                        "ORDER BY etiqueta", nativeQuery = true)
        List<Object[]> agruparVentasPorTiempo(
                        @Param("inicio") Instant inicio,
                        @Param("fin") Instant fin,
                        @Param("formato") String formato,
                        @Param("timeZone") String timeZone);

        @Query(value = "SELECT v FROM Venta v " +
                        "ORDER BY v.fecha DESC", countQuery = "SELECT COUNT(v) FROM Venta v")
        Page<Venta> findAllConRelaciones(Pageable pageable);

        @Query("SELECT v FROM Venta v " +
                        "LEFT JOIN FETCH v.detalles d " +
                        "WHERE v.folio = :folio")
        Optional<Venta> findByFolioConDetalles(@Param("folio") String folio);

        Page<Venta> findByUsuarioIdAndFechaBetweenOrderByFechaDesc(UUID usuarioId, Instant inicio, Instant fin, Pageable pageable);
}
