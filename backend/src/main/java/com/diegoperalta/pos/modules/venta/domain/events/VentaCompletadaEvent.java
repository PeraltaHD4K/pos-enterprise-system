package com.diegoperalta.pos.modules.venta.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VentaCompletadaEvent {
    private final Long ventaId;
    private final String folio;
    private final BigDecimal total;
    private final Instant fecha;
}
