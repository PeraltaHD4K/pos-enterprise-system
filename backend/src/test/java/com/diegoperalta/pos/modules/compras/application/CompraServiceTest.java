package com.diegoperalta.pos.modules.compra.application;

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

import com.diegoperalta.pos.modules.compra.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compra.application.dto.ItemCompraDTO;
import com.diegoperalta.pos.modules.compra.domain.Compra;
import com.diegoperalta.pos.modules.compra.domain.DetalleCompra;
import com.diegoperalta.pos.modules.compra.domain.Proveedor;
import com.diegoperalta.pos.modules.compra.infrastructure.CompraRepository;
import com.diegoperalta.pos.modules.compra.infrastructure.ProveedorRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import java.util.UUID;

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
    private CurrentUserProvider userProvider;

    @InjectMocks
    private CompraService compraService;

    // Datos Dummy
    private Usuario usuarioMock;
    private Proveedor proveedorMock;
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        usuarioMock.setUsername("admin");

        proveedorMock = new Proveedor();
        proveedorMock.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        proveedorMock.setEmpresa("Zorro Abarrotero");

        productoMock = new Producto();
        productoMock.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        productoMock.setNombre("Coca Cola");
        productoMock.setStockActual(0);
        // Configuramos un precio histórico para las pruebas de "Memoria"
        productoMock.setUltimoCostoCompra(new BigDecimal("15.00"));
    }

    @Test
    void registrarCompra_Completada_Manual_NoDuplicaSuma() {
        // --- GIVEN ---
        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        dto.setEstado("COMPLETADA");

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProductoId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        item.setCantidadPedida(10);
        item.setUnidadesPorCaja(1);
        // El usuario ingresa el precio manual (ignora la memoria)
        item.setCostoTotal(new BigDecimal("200.00"));

        dto.setItems(List.of(item));

        // --- STUBBING ---
        when(proveedorRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Optional.of(proveedorMock));
        when(userProvider.getCurrentUserDetails()).thenReturn(usuarioMock);
        when(productoRepository.findAllById(any())).thenReturn(List.of(productoMock));

        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        Compra resultado = compraService.registrarCompra(dto);

        // --- THEN ---
        assertNotNull(resultado);
        // Verificamos que la suma sea EXACTAMENTE 200 (si fuera 400, el bug de doble
        // suma seguiría vivo)
        assertEquals(new BigDecimal("200.00"), resultado.getTotal());

        // Verificar llamada a batch
        verify(productoService, times(1)).registrarEntradasPorCompraBatch(any(), any(), eq(usuarioMock));
    }

    @Test
    void registrarCompra_Pendiente_PrecioCiego_UsaMemoria() {
        // --- GIVEN (Escenario "Jarritos Ciego") ---
        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        dto.setEstado("PENDIENTE");

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProductoId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        item.setCantidadPedida(2); // 2 cajas
        item.setUnidadesPorCaja(24); // 48 piezas total
        item.setCostoTotal(null); // 👈 NULL: El sistema debe calcularlo

        dto.setItems(List.of(item));

        // --- STUBBING ---
        when(userProvider.getCurrentUserDetails()).thenReturn(usuarioMock);
        when(proveedorRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.findAllById(any())).thenReturn(List.of(productoMock));

        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        Compra resultado = compraService.registrarCompra(dto);

        // --- THEN ---
        // El producto tiene ultimoCosto = 15.00 (definido en setUp)
        // Cálculo esperado: 2 cajas * 24 piezas * 15.00 = 720.00
        assertEquals(new BigDecimal("720.00"), resultado.getTotal());

        // Verificar que NO tocó el inventario (porque es PENDIENTE)
        verify(productoService, never()).registrarEntradasPorCompraBatch(any(), any(), any());
    }

    @Test
    void registrarCompra_Pendiente_PrecioCiego_SinMemoria_DevuelveCero() {
        // --- GIVEN ---
        // Producto NUEVO sin historia
        productoMock.setUltimoCostoCompra(null);

        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        dto.setEstado("PENDIENTE");

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProductoId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        item.setCantidadPedida(1);
        item.setCostoTotal(null); // Null y sin historia

        dto.setItems(List.of(item));

        // --- STUBBING ---
        when(userProvider.getCurrentUserDetails()).thenReturn(usuarioMock);
        when(proveedorRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.findAllById(any())).thenReturn(List.of(productoMock));
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
        compraExistente.setId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        compraExistente.setEstado("PENDIENTE");
        compraExistente.setDetalles(new ArrayList<>());

        DetalleCompra detalle = new DetalleCompra();
        detalle.setProductoId(productoMock.getId());
        detalle.setCantidadPedida(10);
        detalle.setUnidadesPorCaja(1);
        detalle.setCostoTotalRenglon(new BigDecimal("200.00")); // Ya venía con precio

        compraExistente.getDetalles().add(detalle);

        when(userProvider.getCurrentUserDetails()).thenReturn(usuarioMock);
        when(compraRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000005"))).thenReturn(Optional.of(compraExistente));
        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);
        when(productoRepository.findById(productoMock.getId())).thenReturn(Optional.of(productoMock));

        // --- WHEN ---
        Compra resultado = compraService.confirmarRecepcion(UUID.fromString("00000000-0000-0000-0000-000000000005"));

        // --- THEN ---
        assertEquals("COMPLETADA", resultado.getEstado());

        // Verificar llamada a batch
        verify(productoService, times(1)).registrarEntradasPorCompraBatch(any(), eq(UUID.fromString("00000000-0000-0000-0000-000000000005")), eq(usuarioMock));
    }
}
