package com.diegoperalta.pos.modules.compras.application.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CompraRegistroDTO {
    private Long proveedorId;
    private String folioFactura;
    private String observaciones;
    private String estado;
    private LocalDate fechaEstimadaEntrega;
    private List<ItemCompraDTO> items;
}
