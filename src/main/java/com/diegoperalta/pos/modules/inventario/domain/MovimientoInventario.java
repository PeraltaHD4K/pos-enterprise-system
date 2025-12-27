package com.diegoperalta.pos.modules.inventario.domain;

import java.time.LocalDateTime;

import com.diegoperalta.pos.modules.iam.domain.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento;

    @Column(length = 255)
    private String motivo;

    private Integer cantidad;

    @Column(name = "stock_anterior")
    private Integer stockAnterior;

    @Column(name = "stock_resultante")
    private Integer stockResultante;

    @Column(name = "referencia_id")
    private Long referenciaId;

    private LocalDateTime fecha = LocalDateTime.now();
}
