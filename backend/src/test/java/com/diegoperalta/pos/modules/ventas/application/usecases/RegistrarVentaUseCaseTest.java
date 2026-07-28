package com.diegoperalta.pos.modules.venta.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.cliente.domain.Cliente;
import com.diegoperalta.pos.modules.cliente.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.inventario.application.ProductoService;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;
import com.diegoperalta.pos.modules.venta.application.dto.ItemVentaDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaRegistroDTO;
import com.diegoperalta.pos.modules.venta.application.dto.VentaResponseDTO;
import com.diegoperalta.pos.modules.venta.domain.Venta;
import com.diegoperalta.pos.modules.venta.infrastructure.VentaRepository;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RegistrarVentaUseCaseTest {

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
    private CurrentUserProvider userProvider;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RegistrarVentaUseCase useCase;

    // Datos dummy para las pruebas
    private Usuario usuarioMock;
    private Cliente clienteMock;
    private SesionCaja sesionMock;
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        // Configuramos datos falsos que simulan venir de la BD
        usuarioMock = new Usuario();
        usuarioMock.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        usuarioMock.setUsername("admin");

        clienteMock = new Cliente();
        clienteMock.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        clienteMock.setNombre("Cliente Test");

        sesionMock = new SesionCaja();
        sesionMock.setEstado("ABIERTA");

        productoMock = new Producto();
        productoMock.setId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        productoMock.setPrecioVenta(new BigDecimal("100.00")); // Precio $100
        productoMock.setCostoPromedio(new BigDecimal("50.00"));
        productoMock.setStockActual(10);
    }

    @Test
    void registrarVenta_DeberiaCalcularTotalCorrectamente() {
        // --- GIVEN (Dado estos datos de entrada...) ---
        VentaRegistroDTO dto = new VentaRegistroDTO();
        dto.setClienteId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        dto.setMetodoPago("EFECTIVO");
        dto.setMontoPagado(new BigDecimal("200.00"));

        ItemVentaDTO item = new ItemVentaDTO();
        item.setProductoId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        item.setCantidad(2); // Compramos 2 unidades
        dto.setItems(List.of(item));

        // --- STUBBING (Cuando el servicio pida datos, devolvemos los mocks...) ---
        when(userProvider.getCurrentUserDetails()).thenReturn(usuarioMock); // Simulamos usuario logueado
        when(sesionCajaRepository.findByUsuarioIdAndEstado(usuarioMock.getId(), "ABIERTA")).thenReturn(Optional.of(sesionMock));
        when(clienteRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findAllById(any())).thenReturn(List.of(productoMock));

        // Simulamos el guardado retornando la misma venta que entra
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN (Ejecutamos la lógica) ---
        VentaResponseDTO ventaResultado = useCase.ejecutar(dto);

        // --- THEN (Verificamos resultados) ---
        assertNotNull(ventaResultado);
        // 2 items * $100 = $200
        assertEquals(new BigDecimal("200.00"), ventaResultado.getTotalVenta());
        assertEquals("COMPLETADA", ventaResultado.getEstado());

        // Verificamos que se llamó al servicio de inventario (batch)
        verify(productoService).registrarSalidasPorVentaBatch(any(), any(), eq(usuarioMock));

        // Verificamos que se guardó la venta (2 veces: inicio y final)
        verify(ventaRepository, org.mockito.Mockito.times(2)).save(any(Venta.class));
    }
}
