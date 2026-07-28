package com.diegoperalta.pos.modules.inventario.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.util.UUID;

@Data
public class ProductoRegistroDTO {
    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    @NotBlank(message = "El código de barras es obligatorio")
    private String codigoBarras;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero(message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    @NotNull(message = "El costo promedio es obligatorio")
    @PositiveOrZero(message = "El costo promedio no puede ser negativo")
    private BigDecimal costoPromedio;

    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @NotNull(message = "La categoría es obligatoria")
    private UUID categoriaId;
}
