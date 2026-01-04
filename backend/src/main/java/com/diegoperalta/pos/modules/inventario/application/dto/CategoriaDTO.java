package com.diegoperalta.pos.modules.inventario.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaDTO {
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    private String nombre;

    private String descripcion;
}
