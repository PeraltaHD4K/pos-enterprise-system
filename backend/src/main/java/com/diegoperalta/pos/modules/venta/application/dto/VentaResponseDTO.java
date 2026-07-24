package com.diegoperalta.pos.modules.venta.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class VentaResponseDTO {
    private Long id;
    private String folio;
    private Instant fecha;
    private String estado;
    private String metodoPago;
    private BigDecimal totalVenta;
    private BigDecimal montoPagado;
    private BigDecimal cambio;
    private String nombreCliente;
    private String nombreVendedor;
    private List<VentaItemResponseDTO> detalles;
}
