package com.diegoperalta.pos.modules.ventas.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diegoperalta.pos.modules.ventas.domain.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

}
