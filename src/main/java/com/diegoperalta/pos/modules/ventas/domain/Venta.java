package com.diegoperalta.pos.modules.ventas.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.clientes.domain.Cliente;
import com.diegoperalta.pos.modules.iam.domain.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String folio; // Ej: V-0001

    @ManyToOne
    @JoinColumn(name = "sesion_caja_id")
    private SesionCaja sesionCaja;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "total_venta", nullable = false)
    private BigDecimal totalVenta;

    @Column(name = "metodo_pago")
    private String metodoPago; // EFECTIVO, TARJETA

    private String estado; // COMPLETADA, CANCELADA

    // RELACIÓN PADRE-HIJO:
    // mappedBy: indica quién manda en la relación (la clase DetalleVenta)
    // cascade ALL: Si guardo la Venta, se guardan sus detalles automáticamente.
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles;
}
