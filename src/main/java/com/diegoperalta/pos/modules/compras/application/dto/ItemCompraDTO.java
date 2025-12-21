package com.diegoperalta.pos.modules.compras.application.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ItemCompraDTO {
    private Long productoId;
    private Integer cantidad;
    private BigDecimal costoUnitario;
}
