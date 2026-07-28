package com.diegoperalta.pos.modules.caja.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import java.util.UUID;

@Repository
public interface SesionCajaRepository extends JpaRepository<SesionCaja, UUID> {
    Optional<SesionCaja> findByUsuarioIdAndEstado(UUID usuarioId, String estado);
}
