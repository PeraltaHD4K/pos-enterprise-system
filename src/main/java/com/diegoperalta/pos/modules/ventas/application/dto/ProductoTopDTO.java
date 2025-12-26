package com.diegoperalta.pos.modules.ventas.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoTopDTO {
    private String nombreProducto;
    private Long cantidadVendida;
    private BigDecimal totalDineroGenerado;
}
