package com.diegoperalta.pos.modules.iam.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nombreCompleto;
    private String username;
    private Boolean activo;
    private String rolNombre;
}
