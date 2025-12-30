package com.diegoperalta.pos.modules.clientes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String telefono;

    @Email(message = "El formato del email es inválido")
    private String email;
}
