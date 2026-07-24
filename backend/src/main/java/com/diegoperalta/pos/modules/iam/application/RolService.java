package com.diegoperalta.pos.modules.iam.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.iam.application.dto.RolResponseDTO;
import com.diegoperalta.pos.modules.iam.domain.Rol;
import com.diegoperalta.pos.modules.iam.infrastructure.RolRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {
    
    private final RolRepository rolRepository;

    public List<RolResponseDTO> listarTodos() {
        return rolRepository.findAll().stream()
            .map(rol -> new RolResponseDTO(rol.getId(), rol.getNombre()))
            .toList();
    }
}
