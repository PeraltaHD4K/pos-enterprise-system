package com.diegoperalta.pos.modules.venta.application.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalesReporteDTO {
    private Long totalTransacciones;
    private BigDecimal totalVenta;
    private BigDecimal totalCosto;
}
