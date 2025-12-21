package com.diegoperalta.pos.modules.ventas.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.clientes.domain.Cliente;
import com.diegoperalta.pos.modules.clientes.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import com.diegoperalta.pos.modules.ventas.application.dto.ItemVentaDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.ventas.domain.DetalleVenta;
import com.diegoperalta.pos.modules.ventas.domain.Venta;
import com.diegoperalta.pos.modules.ventas.infrastructure.VentaRepository;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private ProductoRepository productoRepository; // Para buscar info del producto
    @Autowired
    private ProductoService productoService; // Para descontar stock (Lógica Kardex)
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private SesionCajaRepository sesionCajaRepository;

    @Transactional
    public Venta registrarVenta(VentaRegistroDTO dto) {
        // 1. Obtener Usuario Actual (Hardcodeado ID 1)
        Usuario usuario = usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Validar que tenga CAJA ABIERTA
        SesionCaja sesion = sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .orElseThrow(() -> new RuntimeException("No hay sesión de caja abierta. Abra caja primero."));

        // 3. Obtener Cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 4. Crear Cabecera de Venta
        Venta venta = new Venta();
        venta.setSesionCaja(sesion);
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setMetodoPago(dto.getMetodoPago());
        venta.setEstado("COMPLETADA");
        venta.setFolio(UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Generamos un folio simple
        venta.setDetalles(new ArrayList<>());

        BigDecimal totalAcumulado = BigDecimal.ZERO;

        // 5. Procesar cada Item (Bucle)
        for (ItemVentaDTO item : dto.getItems()) {
            // A. Buscar producto
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoId()));

            // B. Descontar Stock (Llama a tu módulo de Inventario)
            // Esto valida si hay stock y genera el movimiento en el Kardex
            productoService.ajustarStock(producto.getId(), -item.getCantidad(), "VENTA");

            // C. Crear Detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta()); // Precio actual
            detalle.setCostoUnitarioSnapshot(producto.getCostoPromedio()); // Costo actual (Snapshot)

            // Calculo Subtotal
            BigDecimal subtotal = producto.getPrecioVenta().multiply(new BigDecimal(item.getCantidad()));
            detalle.setSubtotal(subtotal);

            // Agregar a la lista y sumar al total
            venta.getDetalles().add(detalle);
            totalAcumulado = totalAcumulado.add(subtotal);
        }

        // 6. Finalizar Venta
        venta.setTotalVenta(totalAcumulado);

        return ventaRepository.save(venta);
    }

}
