package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.diegoperalta.pos.modules.inventario.domain.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByActivoTrue(Pageable pageable);

    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE " +
            "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            "(LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
            "(p.codigoBarras = :query)")
    List<Producto> buscarProductos(@Param("query") String query);

    boolean existsByCodigoBarras(String codigoBarras);

    boolean existsBySku(String sku);

    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE p.stockActual <= p.stockMinimo AND p.activo = true")
    List<Producto> encontrarProductosConStockBajo();
}
