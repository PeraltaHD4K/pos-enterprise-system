package com.diegoperalta.pos.modules.analytics.application.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TicketMetricsDTO {
    @JsonProperty("total_ventas")
    private Integer totalVentas;

    @JsonProperty("ticket_promedio")
    private Double ticketPromedio;

    @JsonProperty("ticket_maximo")
    private Double ticketMaximo;

    @JsonProperty("ticket_minimo")
    private Double ticketMinimo;

    // El histograma: "10-50" -> 5 ventas
    @JsonProperty("distribucion_precios")
    private Map<String, Integer> distribucionPrecios;
}
