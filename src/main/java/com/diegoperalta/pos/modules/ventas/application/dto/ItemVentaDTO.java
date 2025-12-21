package com.diegoperalta.pos.modules.ventas.application.dto;

import lombok.Data;

@Data
public class ItemVentaDTO {
    private Long productoId;
    private Integer cantidad;
}
