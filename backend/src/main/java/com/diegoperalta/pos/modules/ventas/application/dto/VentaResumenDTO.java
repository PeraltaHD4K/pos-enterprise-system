package com.diegoperalta.pos.modules.ventas.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

@Data
public class VentaResumenDTO {
    private Long id;
    private String folio;
    private Instant fecha;
    private BigDecimal totalVenta;
    private String nombreCliente;
    private String nombreVendedor;
    private String estado;
}
