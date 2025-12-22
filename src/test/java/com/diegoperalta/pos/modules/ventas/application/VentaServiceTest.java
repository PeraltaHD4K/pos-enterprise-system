package com.diegoperalta.pos.modules.ventas.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.clientes.domain.Cliente;
import com.diegoperalta.pos.modules.clientes.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import com.diegoperalta.pos.modules.ventas.application.dto.ItemVentaDTO;
import com.diegoperalta.pos.modules.ventas.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.ventas.domain.Venta;
import com.diegoperalta.pos.modules.ventas.infrastructure.VentaRepository;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProductoService productoService;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SesionCajaRepository sesionCajaRepository;
    @Mock
    private UserProvider userProvider; // 👈 Aquí está la clave del refactor

    @InjectMocks
    private VentaService ventaService;

    // Datos dummy para las pruebas
    private Usuario usuarioMock;
    private Cliente clienteMock;
    private SesionCaja sesionMock;
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        // Configuramos datos falsos que simulan venir de la BD
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setUsername("admin");

        clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setNombre("Cliente Test");

        sesionMock = new SesionCaja();
        sesionMock.setEstado("ABIERTA");

        productoMock = new Producto();
        productoMock.setId(10L);
        productoMock.setPrecioVenta(new BigDecimal("100.00")); // Precio $100
        productoMock.setCostoPromedio(new BigDecimal("50.00"));
        productoMock.setStockActual(10);
    }

    @Test
    void registrarVenta_DeberiaCalcularTotalCorrectamente() {
        // --- GIVEN (Dado estos datos de entrada...) ---
        VentaRegistroDTO dto = new VentaRegistroDTO();
        dto.setClienteId(1L);
        dto.setMetodoPago("EFECTIVO");

        ItemVentaDTO item = new ItemVentaDTO();
        item.setProductoId(10L);
        item.setCantidad(2); // Compramos 2 unidades
        dto.setItems(List.of(item));

        // --- STUBBING (Cuando el servicio pida datos, devolvemos los mocks...) ---
        when(userProvider.getCurrentUser()).thenReturn("admin"); // Simulamos usuario logueado
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(sesionCajaRepository.findByUsuarioAndEstado(usuarioMock, "ABIERTA")).thenReturn(Optional.of(sesionMock));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoMock));

        // Simulamos el guardado retornando la misma venta que entra
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN (Ejecutamos la lógica) ---
        Venta ventaResultado = ventaService.registrarVenta(dto);

        // --- THEN (Verificamos resultados) ---
        assertNotNull(ventaResultado);
        // 2 items * $100 = $200
        assertEquals(new BigDecimal("200.00"), ventaResultado.getTotalVenta());
        assertEquals("COMPLETADA", ventaResultado.getEstado());

        // Verificamos que se llamó al servicio de inventario para restar 2 unidades
        verify(productoService).ajustarStock(10L, -2, "VENTA");

        // Verificamos que se guardó la venta
        verify(ventaRepository).save(any(Venta.class));
    }
}
