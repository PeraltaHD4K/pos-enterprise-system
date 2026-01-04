package com.diegoperalta.pos.modules.iam.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AutorizacionDTO {
    @NotBlank(message = "El usuario supervisor es obligatorio")
    private String usernameSupervisor;

    @NotBlank(message = "La contraseña del supervisor es obligatoria")
    private String passwordSupervisor;
}
