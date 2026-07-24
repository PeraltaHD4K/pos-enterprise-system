package com.diegoperalta.pos.modules.caja.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class SesionCajaResponseDTO {
    private Long id;
    private String nombreUsuario;
    private Instant fechaApertura;
    private Instant fechaCierre;
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinalCalculado;
    private BigDecimal saldoFinalReal;
    private BigDecimal diferencia;
    private String estado;
}
