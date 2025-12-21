package com.diegoperalta.pos.modules.inventario.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

}
