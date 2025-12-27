package com.diegoperalta.pos.modules.inventario.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.security.UserProvider;
import com.diegoperalta.pos.modules.inventario.domain.MovimientoInventario;
import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.diegoperalta.pos.modules.inventario.infrastructure.CategoriaRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.MovimientoInventarioRepository;
import com.diegoperalta.pos.modules.inventario.infrastructure.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private MovimientoInventarioRepository movimientoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UserProvider userProvider; // 👈 Mockeamos seguridad

    @InjectMocks
    private ProductoService productoService;

    // Datos dummy
    private Producto productoMock;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Coca Cola");
        productoMock.setStockActual(10); // Empezamos con 10

        usuarioMock = new Usuario();
        usuarioMock.setUsername("almacenista");
        usuarioMock.setId(2L);
    }

    @Test
    void ajustarStock_DeberiaSumarStockYRegistrarMovimiento() {
        // --- GIVEN ---
        Long idProducto = 1L;
        Integer cantidadAjuste = 5; // Agregamos 5

        // --- STUBBING ---
        when(productoRepository.findById(idProducto)).thenReturn(Optional.of(productoMock));

        // Simulamos la seguridad:
        when(userProvider.getCurrentUser()).thenReturn("almacenista");
        when(usuarioRepository.findByUsername("almacenista")).thenReturn(Optional.of(usuarioMock));

        // --- WHEN ---
        Producto resultado = productoService.ajustarStock(idProducto, cantidadAjuste, "AJUSTE_MANUAL",
                "Motivo del ajuste");

        // --- THEN ---
        // 1. El stock debe ser 15 (10 + 5)
        assertEquals(15, resultado.getStockActual());

        // 2. Se debió guardar el movimiento
        verify(movimientoRepository).save(any(MovimientoInventario.class));

        // 3. Se debió guardar el producto actualizado
        verify(productoRepository).save(productoMock);
    }

    @Test
    void ajustarStock_DeberiaLanzarError_SiStockResultanteEsNegativo() {
        // --- GIVEN ---
        Long idProducto = 1L;
        Integer cantidadAjuste = -20; // Queremos quitar 20, pero solo hay 10

        // --- STUBBING ---
        when(productoRepository.findById(idProducto)).thenReturn(Optional.of(productoMock));
        when(userProvider.getCurrentUser()).thenReturn("almacenista");
        when(usuarioRepository.findByUsername("almacenista")).thenReturn(Optional.of(usuarioMock));

        // --- WHEN & THEN ---
        // Esperamos que lance BusinessException
        assertThrows(BusinessException.class, () -> {
            productoService.ajustarStock(idProducto, cantidadAjuste, "AJUSTE_MANUAL", "Motivo del ajuste");
        });

        // Verificamos que NUNCA se guardó nada en la BD porque falló antes
        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }
}