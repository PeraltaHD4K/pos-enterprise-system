package com.diegoperalta.pos.modules.configuracion.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.configuracion.domain.Configuracion;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {

}
