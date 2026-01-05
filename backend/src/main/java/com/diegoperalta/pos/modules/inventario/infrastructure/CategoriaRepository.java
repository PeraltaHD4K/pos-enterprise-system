package com.diegoperalta.pos.modules.inventario.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diegoperalta.pos.modules.inventario.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByActivoTrue();

    boolean existsByNombre(String nombre);
}
