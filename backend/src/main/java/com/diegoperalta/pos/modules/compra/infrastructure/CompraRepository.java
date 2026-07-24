package com.diegoperalta.pos.modules.compra.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.compra.domain.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT c FROM Compra c " +
            "JOIN FETCH c.proveedor " +
            "JOIN FETCH c.usuario " +
            "ORDER BY c.fechaPedido DESC")
    List<Compra> findAllConDatos();

    @Query("SELECT c FROM Compra c " +
            "JOIN FETCH c.proveedor " +
            "JOIN FETCH c.usuario " +
            "JOIN FETCH c.detalles d " +
            "JOIN FETCH d.producto " +
            "WHERE c.id = :id")
    Optional<Compra> findByIdConDetalles(@Param("id") Long id);
}
