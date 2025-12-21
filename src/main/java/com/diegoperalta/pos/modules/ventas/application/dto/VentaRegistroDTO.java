package com.diegoperalta.pos.modules.ventas.application.dto;

import java.util.List;

import lombok.Data;

@Data
public class VentaRegistroDTO {
    private Long clienteId;
    private String metodoPago;
    private List<ItemVentaDTO> items;
}
