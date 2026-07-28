package com.diegoperalta.pos.modules.iam.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private UUID id;
    private String nombreCompleto;
    private String username;
    private Boolean activo;
    private String rolNombre;
}
