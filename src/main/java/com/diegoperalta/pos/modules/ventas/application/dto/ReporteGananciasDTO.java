package com.diegoperalta.pos.modules.ventas.application.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ReporteGananciasDTO {
    private BigDecimal totalVentas;
    private BigDecimal costoVentas;
    private BigDecimal gananciaBruta;
    private BigDecimal margenPorcentaje;
    private Integer totalTransacciones;
    private List<PuntoGraficaDTO> grafica;
}
