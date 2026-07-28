package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diegoperalta.pos.modules.inventario.domain.Categoria;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    List<Categoria> findByActivoTrue();

    boolean existsByNombre(String nombre);
}
