package com.diegoperalta.pos.modules.inventario.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.iam.application.AutorizacionService;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.inventario.application.dto.AjusteStockDTO;
import com.diegoperalta.pos.modules.inventario.application.dto.ProductoRegistroDTO;
import com.diegoperalta.pos.modules.inventario.domain.Categoria;
import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.CategoriaRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.MovimientoInventarioRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {
    public record SalidaVentaInfo(Producto producto, Integer cantidad) {}
    public record EntradaCompraInfo(Producto producto, Integer cantidad, BigDecimal costoCompra) {}

    
    private final ProductoRepository productoRepository;

    
    private final CategoriaRepository categoriaRepository;

    
    private final MovimientoInventarioRepository movimientoRepository;

    
    private final UsuarioRepository usuarioRepository;

    
    private final CurrentUserProvider userProvider;

    
    private final AutorizacionService autorizacionService;

    public Producto crearProducto(ProductoRegistroDTO dto) {
        // 1. Validar que la categoría exista
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(dto.getCategoriaId());

        if (categoriaOptional.isEmpty()) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId());
        }

        // 2. Convertir DTO a Entidad
        Producto producto = new Producto();
        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setCostoPromedio(dto.getCostoPromedio());
        producto.setUltimoCostoCompra(dto.getCostoPromedio());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setStockActual(0);

        // 3. Asignar la relacion con la categoria
        producto.setCategoria(categoriaOptional.get());

        // 4. Guardar el producto
        return productoRepository.save(producto);
    }

    public Page<Producto> listarTodos(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable);
    }

    @Transactional
    public Producto ajustarStock(Long productoId, AjusteStockDTO dto) {
        Usuario usuario = userProvider.getCurrentUserDetails();
        boolean estaAutorizado = "ADMIN".equals(usuario.getRol().getNombre()) ||
                "GERENTE".equals(usuario.getRol().getNombre());

        if (!estaAutorizado) {
            if (dto.getAutorizacion() == null) {
                throw new BusinessException("Se requiere autorizacion de Supervisor para ajustar stock",
                        HttpStatus.FORBIDDEN);
            }
            autorizacionService.validarAutorizacion(dto.getAutorizacion(), "ADMIN", "GERENTE");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        int stockAnterior = producto.getStockActual() == null ? 0 : producto.getStockActual();
        int stockResultante = stockAnterior + dto.getCantidad();

        if (stockResultante < 0) {
            throw new BusinessException("No se puede ajustar el stock a un valor negativo", HttpStatus.BAD_REQUEST);
        }

        producto.setStockActual(stockResultante);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento("AJUSTE_MANUAL");
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockResultante(stockResultante);
        movimiento.setMotivo(dto.getMotivo());

        if (dto.getAutorizacion() != null) {
            movimiento.setReferencia("Autorizado por: " + dto.getAutorizacion().getUsernameSupervisor());
        }

        movimientoRepository.save(movimiento);

        return producto;
    }

    @Transactional
    public void registrarSalidasPorVentaBatch(List<SalidaVentaInfo> salidas, Long ventaId, Usuario usuario) {
        List<MovimientoInventario> movimientos = new java.util.ArrayList<>();
        List<Producto> productosModificados = new java.util.ArrayList<>();

        for (SalidaVentaInfo salida : salidas) {
            Producto producto = salida.producto();
            Integer cantidad = salida.cantidad();

            int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();

            if (stockActual < cantidad) {
                throw new BusinessException("Stock insuficiente para el producto:" + producto.getNombre()
                        + ". Disponible: " + stockActual + ". Cantidad solicitada: " + cantidad, HttpStatus.BAD_REQUEST);
            }

            int nuevoStock = stockActual - cantidad;
            producto.setStockActual(nuevoStock);
            productosModificados.add(producto);

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setUsuario(usuario);
            movimiento.setTipoMovimiento("VENTA");
            movimiento.setCantidad(cantidad * -1);
            movimiento.setStockAnterior(stockActual);
            movimiento.setStockResultante(nuevoStock);
            movimiento.setReferencia(String.valueOf(ventaId));

            movimientos.add(movimiento);
        }

        productoRepository.saveAll(productosModificados);
        movimientoRepository.saveAll(movimientos);
    }

    @Transactional
    public void registrarEntradasPorCompraBatch(List<EntradaCompraInfo> entradas, Long compraId, Usuario usuario) {
        List<MovimientoInventario> movimientos = new java.util.ArrayList<>();
        List<Producto> productosModificados = new java.util.ArrayList<>();

        for (EntradaCompraInfo entrada : entradas) {
            Producto producto = entrada.producto();
            Integer cantidad = entrada.cantidad();
            BigDecimal costoCompra = entrada.costoCompra();

            int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();
            BigDecimal costoPromedioActual = producto.getCostoPromedio() == null ? BigDecimal.ZERO : producto.getCostoPromedio();

            BigDecimal valorInventarioActual = costoPromedioActual.multiply(new BigDecimal(stockActual));
            BigDecimal valorCompraNueva = costoCompra.multiply(new BigDecimal(cantidad));
            int nuevoStockTotal = stockActual + cantidad;

            if (nuevoStockTotal > 0) {
                BigDecimal nuevoCostoPromedio = (valorInventarioActual.add(valorCompraNueva))
                        .divide(new BigDecimal(nuevoStockTotal), 2, java.math.RoundingMode.HALF_UP);
                producto.setCostoPromedio(nuevoCostoPromedio);
            }

            producto.setStockActual(nuevoStockTotal);
            producto.setUltimoCostoCompra(costoCompra);
            productosModificados.add(producto);

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(producto);
            mov.setUsuario(usuario);
            mov.setTipoMovimiento("COMPRA");
            mov.setCantidad(cantidad);
            mov.setStockAnterior(stockActual);
            mov.setStockResultante(nuevoStockTotal);
            mov.setReferencia("COMPRA #" + compraId);

            movimientos.add(mov);
        }

        productoRepository.saveAll(productosModificados);
        movimientoRepository.saveAll(movimientos);
    }

    @Transactional
    public List<Producto> buscarProductos(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return productoRepository.buscarProductos(query.trim());
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerReporteStockBajo() {
        return productoRepository.encontrarProductosConStockBajo();
    }

    @Transactional
    public void devolverStockPorCancelacion(Long productoId, Integer cantidad, String folioVenta) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        int stockAnterior = producto.getStockActual() != null ? producto.getStockActual() : 0;
        int nuevoStock = stockAnterior + cantidad;

        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setUsuario(userProvider.getCurrentUserDetails());
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setMotivo("CANCELACION_VENTA");
        movimiento.setReferencia("FOLIO: " + folioVenta);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockResultante(nuevoStock);

        movimientoRepository.save(movimiento);
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    @Transactional
    public Producto actualizarProducto(Long id, ProductoRegistroDTO dto) {
        Producto producto = obtenerPorId(id);

        // Validar que si cambia el SKU, no choque con otro
        if (!producto.getSku().equalsIgnoreCase(dto.getSku()) && productoRepository.existsBySku(dto.getSku())) {
            throw new BusinessException("Ya existe un producto con el SKU: " + dto.getSku(), HttpStatus.CONFLICT);
        }

        // Validar Código de Barras
        if (!producto.getCodigoBarras().equalsIgnoreCase(dto.getCodigoBarras())
                && productoRepository.existsByCodigoBarras(dto.getCodigoBarras())) {
            throw new BusinessException("Ya existe un producto con el Código de Barras: " + dto.getCodigoBarras(),
                    HttpStatus.CONFLICT);
        }

        // Actualizar campos (Menos stock, ese se mueve por movimientos)
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setSku(dto.getSku());
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setCostoPromedio(dto.getCostoPromedio());
        producto.setStockMinimo(dto.getStockMinimo());

        // Actualizar categoría
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        producto.setCategoria(categoria);

        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Soft Delete
        producto.setActivo(false);
        productoRepository.save(producto);
    }
}
