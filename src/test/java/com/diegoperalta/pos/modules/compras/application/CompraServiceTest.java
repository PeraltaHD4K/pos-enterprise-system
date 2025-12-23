package com.diegoperalta.pos.modules.compras.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diegoperalta.pos.modules.compras.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compras.application.dto.ItemCompraDTO;
import com.diegoperalta.pos.modules.compras.domain.Compra;
import com.diegoperalta.pos.modules.compras.domain.DetalleCompra;
import com.diegoperalta.pos.modules.compras.domain.Proveedor;
import com.diegoperalta.pos.modules.compras.infrastructure.CompraRepository;
import com.diegoperalta.pos.modules.compras.infrastructure.ProveedorRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;
    @Mock
    private ProveedorRepository proveedorRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProductoService productoService;
    @Mock
    private UserProvider userProvider;

    @InjectMocks
    private CompraService compraService;

    // Datos Dummy
    private Usuario usuarioMock;
    private Proveedor proveedorMock;
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setUsername("admin");

        proveedorMock = new Proveedor();
        proveedorMock.setId(1L);
        proveedorMock.setEmpresa("Zorro Abarrotero");

        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Coca Cola");
        productoMock.setStockActual(0);
        // Configuramos un precio histórico para las pruebas de "Memoria"
        productoMock.setUltimoCostoCompra(new BigDecimal("15.00"));
    }

    @Test
    void registrarCompra_Completada_Manual_NoDuplicaSuma() {
        // --- GIVEN ---
        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(1L);
        dto.setEstado("COMPLETADA");

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProductoId(1L);
        item.setCantidadPedida(10);
        item.setUnidadesPorCaja(1);
        // El usuario ingresa el precio manual (ignora la memoria)
        item.setCostoTotal(new BigDecimal("200.00"));

        dto.setItems(List.of(item));

        // --- STUBBING ---
        when(userProvider.getCurrentUser()).thenReturn("admin");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        Compra resultado = compraService.registrarCompra(dto);

        // --- THEN ---
        assertNotNull(resultado);
        // Verificamos que la suma sea EXACTAMENTE 200 (si fuera 400, el bug de doble
        // suma seguiría vivo)
        assertEquals(new BigDecimal("200.00"), resultado.getTotal());

        // Verificar cálculo unitario: 200 / 10 = 20.0000
        verify(productoService, times(1)).registrarEntradaPorCompra(
                eq(1L),
                eq(10),
                eq(new BigDecimal("20.0000")),
                isNull() // El ID es null porque aún no se guarda en BD al llamar al servicio
        );
    }

    @Test
    void registrarCompra_Pendiente_PrecioCiego_UsaMemoria() {
        // --- GIVEN (Escenario "Jarritos Ciego") ---
        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(1L);
        dto.setEstado("PENDIENTE");

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProductoId(1L);
        item.setCantidadPedida(2); // 2 cajas
        item.setUnidadesPorCaja(24); // 48 piezas total
        item.setCostoTotal(null); // 👈 NULL: El sistema debe calcularlo

        dto.setItems(List.of(item));

        // --- STUBBING ---
        when(userProvider.getCurrentUser()).thenReturn("admin");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        Compra resultado = compraService.registrarCompra(dto);

        // --- THEN ---
        // El producto tiene ultimoCosto = 15.00 (definido en setUp)
        // Cálculo esperado: 2 cajas * 24 piezas * 15.00 = 720.00
        assertEquals(new BigDecimal("720.00"), resultado.getTotal());

        // Verificar que NO tocó el inventario (porque es PENDIENTE)
        verify(productoService, never()).registrarEntradaPorCompra(anyLong(), anyInt(), any(), any());
    }

    @Test
    void registrarCompra_Pendiente_PrecioCiego_SinMemoria_DevuelveCero() {
        // --- GIVEN ---
        // Producto NUEVO sin historia
        productoMock.setUltimoCostoCompra(null);

        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(1L);
        dto.setEstado("PENDIENTE");

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProductoId(1L);
        item.setCantidadPedida(1);
        item.setCostoTotal(null); // Null y sin historia

        dto.setItems(List.of(item));

        // --- STUBBING ---
        when(userProvider.getCurrentUser()).thenReturn("admin");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        Compra resultado = compraService.registrarCompra(dto);

        // --- THEN ---
        // Como no hay precio ni memoria, el total debe ser 0
        assertEquals(BigDecimal.ZERO, resultado.getTotal());
    }

    @Test
    void confirmarRecepcion_DeberiaProcesarPendienteYActualizarStock() {
        // --- GIVEN ---
        Compra compraExistente = new Compra();
        compraExistente.setId(5L);
        compraExistente.setEstado("PENDIENTE");
        compraExistente.setDetalles(new ArrayList<>());

        DetalleCompra detalle = new DetalleCompra();
        detalle.setProducto(productoMock);
        detalle.setCantidadPedida(10);
        detalle.setUnidadesPorCaja(1);
        detalle.setCostoTotalRenglon(new BigDecimal("200.00")); // Ya venía con precio

        compraExistente.getDetalles().add(detalle);

        when(compraRepository.findById(5L)).thenReturn(Optional.of(compraExistente));
        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        Compra resultado = compraService.confirmarRecepcion(5L);

        // --- THEN ---
        assertEquals("COMPLETADA", resultado.getEstado());

        // Verificar cálculo: 200 / 10 = 20.0000
        verify(productoService).registrarEntradaPorCompra(
                eq(1L),
                eq(10),
                eq(new BigDecimal("20.0000")),
                eq(5L) // Aquí SÍ hay ID de compra porque ya existía en BD
        );
    }
}
