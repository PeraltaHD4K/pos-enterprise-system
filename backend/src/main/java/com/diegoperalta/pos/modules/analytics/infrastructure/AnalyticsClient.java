package com.diegoperalta.pos.modules.analytics.infrastructure;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.diegoperalta.pos.modules.analytics.application.dto.TicketMetricsDTO;

@Service
public class AnalyticsClient {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsClient.class);
    private final RestClient restClient;
    private final String analyticsUrl;

    public AnalyticsClient(@Qualifier("analyticsRestClient") RestClient restClient,
            @Value("${app.services.analytics-url}") String analyticsUrl) {
        this.restClient = restClient;
        this.analyticsUrl = analyticsUrl;
    }

    public TicketMetricsDTO obtenerMetricasTickets(String start, String end) {
        try {
            String baseUrl = analyticsUrl + "/api/v1/analytics/sales/tickets";

            log.info("📡 Consultando Analytics (Python): {}?start={}&end={}",
                    baseUrl, start, end);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);

            if (start != null)
                uriBuilder.queryParam("start_date", start);
            if (end != null)
                uriBuilder.queryParam("end_date", end);

            URI finalUri = uriBuilder.build().toUri();

            // 3. Hacemos la llamada con la URI final ya fabricada
            return restClient.get()
                    .uri(finalUri)
                    .retrieve()
                    .body(TicketMetricsDTO.class);
        } catch (ResourceAccessException e) {
            log.error("❌ TIMEOUT o Conexión rechazada en Analytics: {}", e.getMessage());
            throw new RuntimeException("El servicio de analítica no está disponible en este momento.");
        } catch (Exception e) {
            log.error("❌ Error obteniendo métricas de Analytics: {}", e.getMessage());
            throw new RuntimeException("Error interno al procesar las analíticas.");
        }
    }
}
