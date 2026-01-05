package com.diegoperalta.pos.modules.inventario.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.modules.iam.application.AutorizacionService;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import com.diegoperalta.pos.modules.inventario.application.dto.AjusteStockDTO;
import com.diegoperalta.pos.modules.inventario.application.dto.ProductoRegistroDTO;
import com.diegoperalta.pos.modules.inventario.domain.Categoria;
import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.CategoriaRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.MovimientoInventarioRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AutorizacionService autorizacionService;

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

    public List<Producto> listarTodos() {
        return productoRepository.findByActivoTrue();
    }

    @Transactional
    public Producto ajustarStock(Long productoId, AjusteStockDTO dto) {
        Usuario usuario = obtenerUsuarioActual();
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
    public void registrarSalidaPorVenta(Long productoId, Integer cantidad, Long ventaId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        Usuario usuario = obtenerUsuarioActual();

        int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();

        if (stockActual < cantidad) {
            throw new BusinessException("Stock insuficiente para el producto:" + producto.getNombre()
                    + ". Disponible: " + stockActual + ". Cantidad solicitada: " + cantidad, HttpStatus.BAD_REQUEST);
        }

        int nuevoStock = stockActual - cantidad;
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento("VENTA");
        movimiento.setCantidad(cantidad * -1);
        movimiento.setStockAnterior(stockActual);
        movimiento.setStockResultante(nuevoStock);
        movimiento.setReferencia(String.valueOf(ventaId));

        movimientoRepository.save(movimiento);
    }

    // Método para procesar entradas de compras y recalcular costos
    @Transactional
    public void registrarEntradaPorCompra(Long productoId, Integer cantidad, BigDecimal costoCompra, Long compraId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // 1. Datos Actuales
        int stockActual = producto.getStockActual() == null ? 0 : producto.getStockActual();
        BigDecimal costoPromedioActual = producto.getCostoPromedio() == null ? BigDecimal.ZERO
                : producto.getCostoPromedio();

        // 2. Calcular Nuevo Costo Promedio (Ponderado)
        // Valor total del inventario actual
        BigDecimal valorInventarioActual = costoPromedioActual.multiply(new BigDecimal(stockActual));
        // Valor de lo que estamos comprando
        BigDecimal valorCompraNueva = costoCompra.multiply(new BigDecimal(cantidad));

        // Nuevo total de unidades
        int nuevoStockTotal = stockActual + cantidad;

        // Nuevo valor total / Nuevas unidades = Nuevo Costo Promedio
        if (nuevoStockTotal > 0) {
            BigDecimal nuevoCostoPromedio = (valorInventarioActual.add(valorCompraNueva))
                    .divide(new BigDecimal(nuevoStockTotal), 2, java.math.RoundingMode.HALF_UP);

            producto.setCostoPromedio(nuevoCostoPromedio);
        }

        Usuario usuario = obtenerUsuarioActual();

        // 3. Actualizar Stock y Guardar
        // (Reusamos la lógica interna, pero sin llamar a ajustarStock para no duplicar
        // kardex si lo manejamos aparte)
        // Aquí simplificaremos llamando directo a los setters para control fino
        producto.setStockActual(nuevoStockTotal);
        producto.setUltimoCostoCompra(costoCompra);
        productoRepository.save(producto);

        // 4. Registrar en Kardex
        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setUsuario(usuario);
        mov.setTipoMovimiento("COMPRA");
        mov.setCantidad(cantidad);
        mov.setStockAnterior(stockActual);
        mov.setStockResultante(nuevoStockTotal);
        mov.setReferencia("COMPRA #" + compraId);

        movimientoRepository.save(mov);
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
        movimiento.setUsuario(obtenerUsuarioActual());
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

    private Usuario obtenerUsuarioActual() {
        String username = userProvider.getCurrentUser();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en la sesión actual"));
    }
}
