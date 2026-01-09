package com.diegoperalta.pos.modules.ventas.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.clientes.domain.Cliente;
import com.diegoperalta.pos.modules.clientes.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.application.AutorizacionService;
import com.diegoperalta.pos.modules.iam.application.dto.AutorizacionDTO;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import com.diegoperalta.pos.modules.ventas.application.dto.ItemVentaDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.ProductoTopDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.PuntoGraficaDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.ReporteGananciasDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.VentaResumenDTO;
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

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AutorizacionService autorizacionService;

    @Transactional
    public Venta registrarVenta(VentaRegistroDTO dto) {
        // 1. Obtener Usuario Actual
        Usuario usuario = obtenerUsuarioActual();

        // 2. Validar que tenga CAJA ABIERTA
        SesionCaja sesion = sesionCajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .orElseThrow(() -> new BusinessException("No hay sesión de caja abierta. Abra caja primero.",
                        HttpStatus.BAD_REQUEST));

        // 3. Obtener Cliente
        Long idClienteParaBuscar = dto.getClienteId();

        if (idClienteParaBuscar == null) {
            // Si no viene cliente, usamos el ID 1 (Público en General creado por el Seeder)
            idClienteParaBuscar = 1L;
        }

        Cliente cliente = clienteRepository.findById(idClienteParaBuscar)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cliente no encontrado con ID: " + dto.getClienteId()));

        // 4. Crear Cabecera de Venta
        Venta venta = new Venta();
        venta.setSesionCaja(sesion);
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setMetodoPago(dto.getMetodoPago());
        venta.setFolio(UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Generamos un folio simple
        venta.setDetalles(new ArrayList<>());
        venta.setEstado("COMPLETADA");
        venta.setTotalVenta(BigDecimal.ZERO);
        venta.setMontoPagado(dto.getMontoPagado());
        venta.setCambio(BigDecimal.ZERO);

        venta = ventaRepository.save(venta);

        BigDecimal totalAcumulado = BigDecimal.ZERO;

        // 5. Procesar cada Item (Bucle)
        for (ItemVentaDTO item : dto.getItems()) {
            // A. Buscar producto
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + item.getProductoId()));

            // B. Descontar Stock (Llama a tu módulo de Inventario)
            // Esto valida si hay stock y genera el movimiento en el Kardex
            productoService.registrarSalidaPorVenta(producto.getId(), item.getCantidad(), venta.getId());

            // C. Crear Detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta()); // Precio actual

            BigDecimal costoSnapshot = producto.getCostoPromedio() != null ? producto.getCostoPromedio()
                    : BigDecimal.ZERO;
            detalle.setCostoUnitarioSnapshot(costoSnapshot); // Costo actual (Snapshot)

            // Calculo Subtotal
            BigDecimal subtotal = producto.getPrecioVenta().multiply(new BigDecimal(item.getCantidad()));
            detalle.setSubtotal(subtotal);

            // Agregar a la lista y sumar al total
            venta.getDetalles().add(detalle);
            totalAcumulado = totalAcumulado.add(subtotal);
        }

        BigDecimal montoPagado = dto.getMontoPagado();

        if (montoPagado.compareTo(totalAcumulado) < 0) {
            throw new BusinessException(
                    String.format("Pago insuficiente. Total: $%s, Pagado $%s", totalAcumulado, montoPagado),
                    HttpStatus.BAD_REQUEST);
        }

        BigDecimal cambio = montoPagado.subtract(totalAcumulado);

        // 6. Finalizar Venta
        venta.setTotalVenta(totalAcumulado);
        venta.setMontoPagado(montoPagado);
        venta.setCambio(cambio);

        return ventaRepository.save(venta);
    }

    private Usuario obtenerUsuarioActual() {
        String username = userProvider.getCurrentUser();

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en la sesión actual"));
    }

    @Transactional(readOnly = true)
    public Page<VentaResumenDTO> listarVentas(Pageable pageable) {
        Page<Venta> paginaVentas = ventaRepository.findAllConRelaciones(pageable);
        return paginaVentas.map(venta -> {
            VentaResumenDTO dto = new VentaResumenDTO();
            dto.setId(venta.getId());
            dto.setFolio(venta.getFolio());
            dto.setFecha(venta.getFecha());
            dto.setTotalVenta(venta.getTotalVenta());
            dto.setEstado(venta.getEstado());

            if (venta.getCliente() != null) {
                dto.setNombreCliente(venta.getCliente().getNombre());
            }

            if (venta.getUsuario() != null) {
                dto.setNombreVendedor(venta.getUsuario().getUsername());
            }

            return dto;
        });
    }

    @Transactional
    public Venta cancelarVenta(String folio, AutorizacionDTO autorizacion) {
        autorizacionService.validarAutorizacion(autorizacion, "ADMIN", "GERENTE");

        Venta venta = ventaRepository.findByFolioConDetalles(folio)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (venta.getEstado().equals("CANCELADA")) {
            throw new BusinessException("La venta ya se encuentra cancelada", HttpStatus.BAD_REQUEST);
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            productoService.devolverStockPorCancelacion(
                    detalle.getProducto().getId(),
                    detalle.getCantidad(),
                    venta.getFolio());
        }

        venta.setEstado("CANCELADA");

        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public ReporteGananciasDTO generarReporteGanancias(LocalDateTime inicio, LocalDateTime fin) {
        List<Venta> ventas = ventaRepository.buscarVentasEnRango(inicio, fin);

        BigDecimal totalVenta = BigDecimal.ZERO;
        BigDecimal totalCosto = BigDecimal.ZERO;

        for (Venta venta : ventas) {
            totalVenta = totalVenta.add(venta.getTotalVenta());

            for (DetalleVenta detalle : venta.getDetalles()) {
                BigDecimal costoUnitario = detalle.getCostoUnitarioSnapshot() != null
                        ? detalle.getCostoUnitarioSnapshot()
                        : BigDecimal.ZERO;

                BigDecimal costoRenglon = costoUnitario.multiply(new BigDecimal(detalle.getCantidad()));
                totalCosto = totalCosto.add(costoRenglon);
            }
        }

        List<PuntoGraficaDTO> datosGrafica = calcularDatosGrafica(ventas, inicio, fin);

        ReporteGananciasDTO reporte = new ReporteGananciasDTO();
        reporte.setTotalVentas(totalVenta);
        reporte.setCostoVentas(totalCosto);
        reporte.setGananciaBruta(totalVenta.subtract(totalCosto));
        reporte.setTotalTransacciones(ventas.size());
        reporte.setGrafica(datosGrafica);

        if (totalVenta.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = reporte.getGananciaBruta()
                    .divide(totalVenta, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
            reporte.setMargenPorcentaje(margen);
        } else {
            reporte.setMargenPorcentaje(BigDecimal.ZERO);
        }

        if (reporte.getTotalTransacciones() != null && reporte.getTotalTransacciones() > 0) {
            BigDecimal promedio = reporte.getTotalVentas()
                    .divide(new BigDecimal(reporte.getTotalTransacciones()), 2, RoundingMode.HALF_UP);
            reporte.setTicketPromedio(promedio);
        } else {
            reporte.setTicketPromedio(BigDecimal.ZERO);
        }

        return reporte;
    }

    private List<PuntoGraficaDTO> calcularDatosGrafica(List<Venta> ventas, LocalDateTime inicio, LocalDateTime fin) {
        boolean esUnSoloDia = inicio.toLocalDate().isEqual(fin.toLocalDate());

        DateTimeFormatter formatter = esUnSoloDia
                ? DateTimeFormatter.ofPattern("HH:00")
                : DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, List<Venta>> grupos = new TreeMap<>();

        for (Venta venta : ventas) {
            String clave = venta.getFecha().format(formatter);
            grupos.computeIfAbsent(clave, k -> new ArrayList<>()).add(venta);
        }

        List<PuntoGraficaDTO> puntos = new ArrayList<>();

        for (Map.Entry<String, List<Venta>> entrada : grupos.entrySet()) {
            String etiqueta = entrada.getKey();
            List<Venta> ventasEnGrupo = entrada.getValue();

            BigDecimal sumaVenta = BigDecimal.ZERO;
            BigDecimal sumaCosto = BigDecimal.ZERO;

            for (Venta venta : ventasEnGrupo) {
                sumaVenta = sumaVenta.add(venta.getTotalVenta());

                for (DetalleVenta detalle : venta.getDetalles()) {
                    BigDecimal costoUnitario = detalle.getCostoUnitarioSnapshot() != null
                            ? detalle.getCostoUnitarioSnapshot()
                            : BigDecimal.ZERO;
                    sumaCosto = sumaCosto.add(costoUnitario.multiply(new BigDecimal(detalle.getCantidad())));
                }
            }

            BigDecimal gananciaGrupo = sumaVenta.subtract(sumaCosto);

            puntos.add(new PuntoGraficaDTO(etiqueta, sumaVenta, gananciaGrupo, ventasEnGrupo.size()));
        }

        return puntos;
    }

    @Transactional(readOnly = true)
    public List<ProductoTopDTO> obtenerTopProductos(LocalDateTime inicio, LocalDateTime fin, int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return ventaRepository.encontrarTopProductos(inicio, fin, pageable);
    }
}
