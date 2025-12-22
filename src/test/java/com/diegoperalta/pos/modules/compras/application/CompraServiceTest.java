package com.diegoperalta.pos.modules.compras.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.modules.compras.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compras.application.dto.ItemCompraDTO;
import com.diegoperalta.pos.modules.compras.domain.Compra;
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
    private ProductoService productoService; // 👈 Mockeamos el servicio de inventario
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
    }

    @Test
    void registrarCompra_DeberiaCalcularTotalYActualizarInventario() {
        // --- GIVEN ---
        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(1L);
        dto.setFolioFactura("FACT-001");

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProductoId(1L);
        item.setCantidad(100);
        item.setCostoUnitario(new BigDecimal("15.00"));

        dto.setItems(List.of(item));

        // --- STUBBING ---
        when(userProvider.getCurrentUser()).thenReturn("admin");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        // Simulamos el guardado
        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        Compra resultado = compraService.registrarCompra(dto);

        // --- THEN ---
        assertNotNull(resultado);
        // Verificamos Matemáticas: 100 * 15.00 = 1500.00
        assertEquals(new BigDecimal("1500.00"), resultado.getTotal());

        // 🔥 CRÍTICO: Verificar que llamó al servicio de Producto para el Kardex
        verify(productoService, times(1)).registrarEntradaPorCompra(
                eq(1L), // ID Producto
                eq(100), // Cantidad
                eq(new BigDecimal("15.00")) // Costo
        );

        verify(compraRepository).save(any(Compra.class));
    }

    @Test
    void registrarCompra_DeberiaFallar_SiListaItemsVacia() {
        // --- GIVEN ---
        CompraRegistroDTO dto = new CompraRegistroDTO();
        dto.setProveedorId(1L);
        dto.setItems(Collections.emptyList()); // Lista vacía

        // --- WHEN & THEN ---
        assertThrows(BusinessException.class, () -> compraService.registrarCompra(dto));

        verify(compraRepository, never()).save(any());
    }
}
