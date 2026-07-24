package com.diegoperalta.pos.modules.inventario.application.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioResponseDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private String tipoMovimiento;
    private String motivo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockResultante;
    private String referencia;
    private Instant fecha;
}
