package com.diegoperalta.pos.modules.ventas.application.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class VentaRegistroDTO {
    private Long clienteId; // Opcional, puede ser null para publico en general

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    @Valid
    private List<ItemVentaDTO> items;

    @NotNull(message = "El monto pagado es obligatorio")
    @PositiveOrZero(message = "El monto pagado no puede ser negativo")
    private BigDecimal montoPagado;
}
