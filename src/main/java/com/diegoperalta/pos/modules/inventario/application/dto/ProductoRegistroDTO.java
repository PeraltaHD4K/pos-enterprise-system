package com.diegoperalta.pos.modules.inventario.application.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoRegistroDTO {
    private String sku;
    private String codigoBarras;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private BigDecimal costoPromedio;
    private Integer stockMinimo;
    private Long categoriaId;
}
