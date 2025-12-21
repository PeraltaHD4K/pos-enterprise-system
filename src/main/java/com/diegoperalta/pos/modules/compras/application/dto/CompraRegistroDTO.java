package com.diegoperalta.pos.modules.compras.application.dto;

import java.util.List;

import lombok.Data;

@Data
public class CompraRegistroDTO {
    private Long proveedorId;
    private String folioFactura;
    private List<ItemCompraDTO> items;
}
