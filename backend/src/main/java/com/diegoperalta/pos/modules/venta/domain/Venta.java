package com.diegoperalta.pos.modules.venta.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.diegoperalta.pos.modules.caja.domain.SesionCaja;
import com.diegoperalta.pos.modules.cliente.domain.Cliente;
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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;
import com.diegoperalta.pos.common.domain.AuditableEntity;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@SQLDelete(sql = "UPDATE ventas SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Entity
@Table(name = "ventas")
public class Venta extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    private String folio; // Ej: V-0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_caja_id")
    @ToString.Exclude
    private SesionCaja sesionCaja;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    private Instant fecha = Instant.now();

    @Column(name = "total_venta", nullable = false)
    private BigDecimal totalVenta;

    @Column(name = "metodo_pago")
    private String metodoPago; // EFECTIVO, TARJETA

    private String estado; // COMPLETADA, CANCELADA

    // RELACIÓN PADRE-HIJO:
    // mappedBy: indica quién manda en la relación (la clase DetalleVenta)
    // cascade ALL: Si guardo la Venta, se guardan sus detalles automáticamente.
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(name = "monto_pagado", nullable = false)
    private BigDecimal montoPagado;

    @Column(name = "cambio")
    private BigDecimal cambio;

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }
}
