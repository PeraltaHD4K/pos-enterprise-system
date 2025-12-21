package com.diegoperalta.pos.modules.compras.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.compras.domain.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
}
