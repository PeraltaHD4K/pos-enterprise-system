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
@SQLDelete(sql = "UPDATE movimientos_caja SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Entity
@Table(name = "movimientos_caja")
public class MovimientoCaja extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_caja_id", nullable = false)
    @ToString.Exclude
    private SesionCaja sesionCaja;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String tipo; // 'INGRESO' (Entrada dinero) o 'RETIRO' (Salida dinero)

    private String motivo;

    private Instant fecha = Instant.now();
}
