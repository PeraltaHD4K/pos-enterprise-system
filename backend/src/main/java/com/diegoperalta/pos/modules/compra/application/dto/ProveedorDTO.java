package com.diegoperalta.pos.modules.compra.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String empresa;

    private String contacto;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "El formato del correo no es válido")
    private String email;

    private String diaVisita;
}