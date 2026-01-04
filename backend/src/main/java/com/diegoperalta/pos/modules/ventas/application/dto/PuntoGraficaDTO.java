package com.diegoperalta.pos.modules.ventas.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PuntoGraficaDTO {
    private String etiqueta;
    private BigDecimal totalVentas;
    private BigDecimal ganancia;
    private Integer cantidadVentas;
}
