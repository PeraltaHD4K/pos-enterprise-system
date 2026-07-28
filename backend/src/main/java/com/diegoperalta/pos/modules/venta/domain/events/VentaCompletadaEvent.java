package com.diegoperalta.pos.modules.venta.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class VentaCompletadaEvent {
    private final UUID ventaId;
    private final String folio;
    private final BigDecimal total;
    private final Instant fecha;
}
