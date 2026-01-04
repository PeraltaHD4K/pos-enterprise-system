package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NuevoMovimientoCajaDTO {
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotBlank(message = "El tipo (INGRESO/RETIRO) es obligatorio")
    private String tipo;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}
