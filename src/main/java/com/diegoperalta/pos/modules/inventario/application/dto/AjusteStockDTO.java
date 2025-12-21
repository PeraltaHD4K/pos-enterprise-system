package com.diegoperalta.pos.modules.inventario.application.dto;

import lombok.Data;

@Data
public class AjusteStockDTO {
    private Integer cantidad;
    private String motivo;
}
