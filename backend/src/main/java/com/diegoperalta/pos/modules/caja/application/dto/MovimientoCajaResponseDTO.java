package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaResponseDTO {
    private UUID id;
    private UUID sesionCajaId;
    private String usuarioNombre;
    private BigDecimal monto;
    private String tipo;
    private String motivo;
    private Instant fecha;
}
