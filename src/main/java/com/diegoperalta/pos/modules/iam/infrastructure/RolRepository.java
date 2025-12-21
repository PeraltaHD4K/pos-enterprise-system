package com.diegoperalta.pos.modules.iam.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.iam.domain.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

}
