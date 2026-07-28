package com.diegoperalta.pos.modules.cliente.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.diegoperalta.pos.modules.cliente.domain.Cliente;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "c.telefono LIKE CONCAT('%', :query, '%')")
    List<Cliente> buscarClientes(@Param("query") String query);

    Optional<Cliente> findByNombre(String nombre);

    boolean existsByEmail(String email);
}
