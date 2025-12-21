package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class NuevoMovimientoCajaDTO {
    private BigDecimal monto;
    private String tipo;
    private String motivo;
}
