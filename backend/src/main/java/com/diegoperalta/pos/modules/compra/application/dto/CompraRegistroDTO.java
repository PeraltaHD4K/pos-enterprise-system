package com.diegoperalta.pos.modules.compra.application.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompraRegistroDTO {

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    private String folioFactura;

    private String observaciones;

    private String estado;

    private LocalDate fechaEstimadaEntrega;

    @NotEmpty(message = "La compra debe tener al menos un producto")
    @Valid
    private List<ItemCompraDTO> items;
}
