package com.diegoperalta.pos.modules.caja.domain;

import java.math.BigDecimal;
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
@Table(name = "movimientos_caja")
public class MovimientoCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sesion_caja_id")
    private SesionCaja sesionCaja;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String tipo; // 'INGRESO' (Entrada dinero) o 'RETIRO' (Salida dinero)

    private String motivo;

    private LocalDateTime fecha = LocalDateTime.now();
}
