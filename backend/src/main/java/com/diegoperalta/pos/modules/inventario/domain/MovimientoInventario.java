package com.diegoperalta.pos.modules.inventario.domain;

import java.time.Instant;

import com.diegoperalta.pos.modules.iam.domain.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @ToString.Exclude
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @ToString.Exclude
    private Usuario usuario;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento;

    @Column(length = 255)
    private String motivo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior")
    private Integer stockAnterior;

    @Column(name = "stock_resultante")
    private Integer stockResultante;

    // @Column(name = "referencia_id")
    // private Long referenciaId;

    @Column(name = "referencia")
    private String referencia;

    @Column(nullable = false)
    private Instant fecha = Instant.now();
}
