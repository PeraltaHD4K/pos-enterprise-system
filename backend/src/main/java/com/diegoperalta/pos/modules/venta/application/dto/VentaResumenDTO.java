package com.diegoperalta.pos.modules.venta.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;
import java.util.UUID;

@Data
public class VentaResumenDTO {
    private UUID id;
    private String folio;
    private Instant fecha;
    private BigDecimal totalVenta;
    private String nombreCliente;
    private String nombreVendedor;
    private String estado;
}
