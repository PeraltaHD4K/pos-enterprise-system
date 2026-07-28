package com.diegoperalta.pos.modules.compra.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.compra.domain.Proveedor;
import java.util.UUID;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {
    List<Proveedor> findByActivoTrue();

    boolean existsByEmpresa(String empresa);
}
