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
import java.util.UUID;
import com.diegoperalta.pos.common.domain.AuditableEntity;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SQLDelete(sql = "UPDATE sesiones_caja SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Entity
@Table(name = "sesiones_caja")
public class SesionCaja extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

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
