package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CierreCajaDTO {
    @NotNull(message = "El saldo final real es obligatorio")
    @PositiveOrZero(message = "El saldo final no puede ser negativo")
    private BigDecimal saldoFinalReal;
}
