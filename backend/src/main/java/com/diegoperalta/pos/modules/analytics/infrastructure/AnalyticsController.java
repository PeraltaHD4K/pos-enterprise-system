package com.diegoperalta.pos.modules.analytics.infrastructure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.analytics.application.dto.TicketMetricsDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {


    private final AnalyticsClient analyticsClient;

    @GetMapping("/dashboard/tickets")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<?> getDashboardTicketMetrics(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        TicketMetricsDTO metrics = analyticsClient.obtenerMetricasTickets(start, end);
        return ResponseEntity.ok(metrics);
    }
}
