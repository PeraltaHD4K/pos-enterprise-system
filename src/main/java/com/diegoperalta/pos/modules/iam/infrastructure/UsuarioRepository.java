package com.diegoperalta.pos.modules.iam.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diegoperalta.pos.modules.iam.domain.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.username = :username")
    Optional<Usuario> findByUsername(@Param("username") String username);

    boolean existsByUsername(String username);
}
