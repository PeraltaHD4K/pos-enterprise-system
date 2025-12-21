package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CierreCajaDTO {
    private BigDecimal saldoFinalReal;
}
