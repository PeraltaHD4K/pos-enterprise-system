package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diegoperalta.pos.modules.inventario.domain.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findBySku(String sku);
}
