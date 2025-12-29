package com.diegoperalta.pos.modules.ventas.application;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.ventas.domain.DetalleVenta;
import com.diegoperalta.pos.modules.ventas.domain.Venta;
import com.diegoperalta.pos.modules.ventas.infrastructure.VentaRepository;

@Service
public class TicketService {
    @Autowired
    private VentaRepository ventaRepository;

    private static final int ANCHO_TICKET = 32;
    private static final String LINEA_DIVISORIA = "--------------------------------\\n";

    @Transactional
    public String generarContenidoTicket(String folio) {
        Venta venta = ventaRepository.findByFolioConDetalles(folio)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + folio));

        StringBuilder ticket = new StringBuilder();

        centrarTexto(ticket, "MI TIENDA POS");
        centrarTexto(ticket, "Sucursal: Centro");
        centrarTexto(ticket, "RFC XXXXXXXXXXXXXX");
        ticket.append("\n");

        ticket.append("Folio: ").append(venta.getFolio()).append("\n");
        ticket.append("Fecha: ").append(venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .append("\n");
        ticket.append("Cajero: ").append(venta.getUsuario().getUsername()).append("\n");
        ticket.append("Cliente: ").append(venta.getCliente().getNombre()).append("\n");

        ticket.append(LINEA_DIVISORIA);
        ticket.append("CANT  PRODUCTO           TOTAL\n");
        ticket.append(LINEA_DIVISORIA);

        for (DetalleVenta detalle : venta.getDetalles()) {
            String nombreProducto = detalle.getProducto().getNombre();

            if (nombreProducto.length() > 18) {
                nombreProducto = nombreProducto.substring(0, 18);
            }

            String linea = String.format("%-4s %-18s %7s\n",
                    detalle.getCantidad(),
                    nombreProducto,
                    String.format("%.2f", detalle.getSubtotal()));

            ticket.append(linea);
        }

        ticket.append(LINEA_DIVISORIA);

        String totalStr = String.format("TOTAL: $ %s", venta.getTotalVenta());
        alinearDerecha(ticket, totalStr);

        ticket.append("\n");
        centrarTexto(ticket, "¡GRACIAS POR SU COMPRA!");
        ticket.append("\n\n\n");

        return ticket.toString();
    }

    private void centrarTexto(StringBuilder sb, String texto) {
        int espacios = (ANCHO_TICKET - texto.length()) / 2;

        for (int i = 0; i < espacios; i++) {
            sb.append(" ");
        }
        sb.append(texto).append("\n");
    }

    private void alinearDerecha(StringBuilder sb, String texto) {
        int espacios = ANCHO_TICKET - texto.length();
        for (int i = 0; i < espacios; i++) {
            sb.append(" ");
        }
        sb.append(texto).append("\n");
    }
}
