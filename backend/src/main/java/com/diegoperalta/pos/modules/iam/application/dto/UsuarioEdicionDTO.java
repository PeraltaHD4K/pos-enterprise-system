package com.diegoperalta.pos.modules.iam.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class UsuarioEdicionDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El usuario es obligatorio")
    private String username;

    // Password es opcional en edición. Si viene null/vacío, no se cambia.
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private UUID rolId;
}
