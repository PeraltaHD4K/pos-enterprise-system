package com.diegoperalta.pos.modules.iam.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.iam.domain.Rol;
import com.diegoperalta.pos.modules.iam.infrastructure.RolRepository;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }
}
