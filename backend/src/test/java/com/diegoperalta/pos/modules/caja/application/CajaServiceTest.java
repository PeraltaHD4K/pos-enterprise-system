package com.diegoperalta.pos.modules.caja.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.modules.caja.application.dto.CierreCajaDTO;
import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.caja.infrastructure.MovimientoCajaRepository; // 👈 Importar
import com.diegoperalta.pos.modules.caja.infrastructure.SesionCajaRepository;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;
import com.diegoperalta.pos.modules.iam.application.ports.CurrentUserProvider;
import com.diegoperalta.pos.modules.venta.infrastructure.VentaRepository;

@ExtendWith(MockitoExtension.class)
class CajaServiceTest {

    @Mock
    private SesionCajaRepository sesionCajaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private MovimientoCajaRepository movimientoCajaRepository;
    @Mock
    private CurrentUserProvider userProvider;

    @InjectMocks
    private CajaService cajaService;

    // Datos dummy
    private Usuario usuarioMock;
    private SesionCaja sesionAbiertaMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setUsername("cajero");

        sesionAbiertaMock = new SesionCaja();
        sesionAbiertaMock.setId(5L);
        sesionAbiertaMock.setUsuario(usuarioMock);
        sesionAbiertaMock.setSaldoInicial(new BigDecimal("500.00"));
        sesionAbiertaMock.setEstado("ABIERTA");
    }

    @Test
    void cerrarCaja_DeberiaCalcularTotalConVentasYCerrar() {
        // --- GIVEN ---
        CierreCajaDTO dto = new CierreCajaDTO();
        dto.setSaldoFinalReal(new BigDecimal("700.00"));

        // --- STUBBING ---
        when(userProvider.getCurrentUserDetails()).thenReturn(usuarioMock);
        when(sesionCajaRepository.findByUsuarioAndEstado(usuarioMock, "ABIERTA"))
                .thenReturn(Optional.of(sesionAbiertaMock));

        // Simulamos VENTAS por $200
        when(ventaRepository.sumarVentasEfectivo(sesionAbiertaMock))
                .thenReturn(new BigDecimal("200.00"));

        // 👇 2. AGREGAR ESTO: Simulamos que NO hubo movimientos extra (Ingresos/Retiros
        // = 0)
        // Si no ponemos esto, Mockito devuelve NULL y las sumas fallan
        when(movimientoCajaRepository.sumarPorSesionYTipo(sesionAbiertaMock, "INGRESO")).thenReturn(BigDecimal.ZERO);
        when(movimientoCajaRepository.sumarPorSesionYTipo(sesionAbiertaMock, "RETIRO")).thenReturn(BigDecimal.ZERO);

        // Simulamos guardado
        when(sesionCajaRepository.save(any(SesionCaja.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- WHEN ---
        SesionCaja resultado = cajaService.cerrarCaja(dto);

        // --- THEN ---
        assertEquals("CERRADA", resultado.getEstado());
        assertEquals(new BigDecimal("700.00"), resultado.getSaldoFinalCalculado());
        assertEquals(new BigDecimal("0.00"), resultado.getDiferencia());

        verify(sesionCajaRepository).save(any(SesionCaja.class));
    }

    @Test
    void cerrarCaja_DeberiaLanzarError_SiNoExisteSesionAbierta() {
        CierreCajaDTO dto = new CierreCajaDTO();

        when(userProvider.getCurrentUserDetails()).thenReturn(usuarioMock);

        when(sesionCajaRepository.findByUsuarioAndEstado(usuarioMock, "ABIERTA"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> cajaService.cerrarCaja(dto));

        verify(sesionCajaRepository, never()).save(any());
    }
}
