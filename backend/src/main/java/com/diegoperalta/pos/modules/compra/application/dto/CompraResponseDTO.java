package com.diegoperalta.pos.modules.compra.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import java.util.UUID;

@Data
public class CompraResponseDTO {
    private UUID id;
    private String folioFactura;
    private String nombreProveedor;
    private String nombreUsuario;
    private Instant fechaPedido;
    private Instant fechaRecepcion;
    private LocalDate fechaEstimadaEntrega;
    private String estado;
    private BigDecimal total;
    private String observaciones;
}
