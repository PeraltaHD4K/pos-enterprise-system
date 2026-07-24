package com.diegoperalta.pos.modules.venta.application.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class VentaItemResponseDTO {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
