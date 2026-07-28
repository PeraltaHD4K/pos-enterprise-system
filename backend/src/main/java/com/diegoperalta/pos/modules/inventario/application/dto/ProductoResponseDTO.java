package com.diegoperalta.pos.modules.inventario.application.dto;

import java.math.BigDecimal;
import lombok.Data;
import java.util.UUID;

@Data
public class ProductoResponseDTO {
    private UUID id;
    private String sku;
    private String codigoBarras;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private BigDecimal costoPromedio;
    private BigDecimal ultimoCostoCompra;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean activo;
    private String categoriaNombre;
    private UUID categoriaId;
}
