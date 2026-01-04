package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CorteXDTO {
    private BigDecimal saldoInicial;
    private BigDecimal ventasEfectivo;
    private BigDecimal ventasOtrosMetodos; // Tarjeta, vales, etc.
    private BigDecimal totalIngresos; // Movimientos manuales
    private BigDecimal totalRetiros; // Movimientos manuales
    private BigDecimal saldoEsperadoEnCaja; // Lo que debería tener en billetes
}