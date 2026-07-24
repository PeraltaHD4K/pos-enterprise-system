package com.diegoperalta.pos.modules.venta.application;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.common.utils.TicketBuilder;
import com.diegoperalta.pos.modules.configuracion.application.ConfiguracionService;
import com.diegoperalta.pos.modules.venta.domain.DetalleVenta;
import com.diegoperalta.pos.modules.venta.domain.Venta;
import com.diegoperalta.pos.modules.venta.infrastructure.VentaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final VentaRepository ventaRepository;

    
    private final ConfiguracionService configService;

    @Value("${app.business.time-zone:UTC}")
    private String businessTimeZone;

    private static final int ANCHO_TICKET = 32;
    private static final String LINEA_DIVISORIA = "--------------------------------\n";

    @Transactional
    public String generarContenidoTicket(String folio) {
        Venta venta = ventaRepository.findByFolioConDetalles(folio)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + folio));

        Map<String, String> config = configService.obtenerConfiguracionCompleta();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = venta.getFecha()
                .atZone(ZoneId.of(businessTimeZone))
                .format(formatter);

        TicketBuilder tb = new TicketBuilder(ANCHO_TICKET);

        tb.centrar(config.getOrDefault("NOMBRE_TIENDA", "MI TIENDA POS"));
        if (config.containsKey("DIRECCION")) {
            tb.centrar(config.get("DIRECCION"));
        }
        tb.centrar("RFC: " + config.getOrDefault("RFC", "XAXX010101000"))
          .saltoDeLinea()
          .texto("Folio: ").texto(venta.getFolio()).saltoDeLinea()
          .texto("Fecha: ").texto(fechaFormateada).saltoDeLinea()
          .texto("Cajero: ").texto(venta.getUsuario().getUsername()).saltoDeLinea()
          .texto("Cliente: ").texto(venta.getCliente().getNombre()).saltoDeLinea()
          .lineaDivisoria()
          .texto("CANT  PRODUCTO           TOTAL\n")
          .lineaDivisoria();

        for (DetalleVenta detalle : venta.getDetalles()) {
            tb.itemLista(
                detalle.getCantidad().toString(), 
                detalle.getProducto().getNombre(), 
                String.format("%.2f", detalle.getSubtotal())
            );
        }

        tb.lineaDivisoria()
          .alinearDerecha("TOTAL: $ " + venta.getTotalVenta())
          .alinearDerecha("PAGADO:  $ " + venta.getMontoPagado())
          .alinearDerecha("CAMBIO:  $ " + venta.getCambio())
          .saltoDeLinea()
          .centrar(config.getOrDefault("TICKET_FOOTER", "¡Gracias por su compra!"))
          .saltosDeLinea(3);

        return tb.build();
    }
}
