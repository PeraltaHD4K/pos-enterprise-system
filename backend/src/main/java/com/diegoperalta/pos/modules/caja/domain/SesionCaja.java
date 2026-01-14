package com.diegoperalta.pos.modules.caja.domain;

import java.math.BigDecimal;
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
@Table(name = "sesiones_caja")
public class SesionCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @Column(name = "fecha_apertura")
    private Instant fechaApertura = Instant.now();

    @Column(name = "fecha_cierre")
    private Instant fechaCierre;

    @Column(name = "saldo_inicial")
    private BigDecimal saldoInicial;

    @Column(name = "saldo_final_calculado")
    private BigDecimal saldoFinalCalculado;

    @Column(name = "saldo_final_real")
    private BigDecimal saldoFinalReal;

    @Column(name = "diferencia")
    private BigDecimal diferencia;

    private String estado;
}
