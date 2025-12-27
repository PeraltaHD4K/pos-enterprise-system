package com.diegoperalta.pos.modules.caja.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "movimientos_caja")
public class MovimientoCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_caja_id", nullable = false)
    @ToString.Exclude
    private SesionCaja sesionCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String tipo; // 'INGRESO' (Entrada dinero) o 'RETIRO' (Salida dinero)

    private String motivo;

    private LocalDateTime fecha = LocalDateTime.now();
}
