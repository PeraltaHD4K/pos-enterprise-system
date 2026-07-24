package com.diegoperalta.pos.modules.analytics.infrastructure.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.diegoperalta.pos.modules.venta.domain.events.VentaCompletadaEvent;

@Component
public class VentaEventListener {
    @Async
    @EventListener
    public void alCompletarseVenta(VentaCompletadaEvent event) {
        // Simulamos una tarea pesada (ej. recalcular IA, mandar correo, etc.)
        try {
            System.out.println("⚡ EVENTO RECIBIDO (Async): Venta " + event.getFolio() + " por $" + event.getTotal());

            // Aquí en el futuro llamaremos a Python:
            // analyticsClient.notificarNuevaVenta(event.getVentaId());

        } catch (Exception e) {
            System.err.println("Error procesando evento de venta: " + e.getMessage());
        }
    }
}
