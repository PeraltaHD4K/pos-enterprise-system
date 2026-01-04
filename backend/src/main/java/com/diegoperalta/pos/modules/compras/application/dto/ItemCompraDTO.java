package com.diegoperalta.pos.modules.compras.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ItemCompraDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad pedida es obligatoria")
    @Min(value = 1, message = "La cantidad pedida debe ser al menos 1")
    private Integer cantidadPedida;

    private Integer cantidadRecibida;

    @Min(value = 1, message = "Las unidades por caja deben ser al menos 1")
    private Integer unidadesPorCaja;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private BigDecimal costoTotal;
}
