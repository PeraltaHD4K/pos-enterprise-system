package com.diegoperalta.pos.modules.venta.application.dto;

import java.math.BigDecimal;
import lombok.Data;
import java.util.UUID;

@Data
public class VentaItemResponseDTO {
    private UUID productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
