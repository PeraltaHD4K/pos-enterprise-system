package com.diegoperalta.pos.modules.inventario.application.dto;

import com.diegoperalta.pos.modules.iam.application.dto.AutorizacionDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AjusteStockDTO {
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    private AutorizacionDTO autorizacion; // Opcional: Solo si quien hace el ajuste requiere autorizacion
}
