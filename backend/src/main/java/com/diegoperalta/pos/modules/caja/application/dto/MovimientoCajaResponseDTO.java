package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaResponseDTO {
    private Long id;
    private Long sesionCajaId;
    private String usuarioNombre;
    private BigDecimal monto;
    private String tipo;
    private String motivo;
    private Instant fecha;
}
