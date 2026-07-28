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
@SQLDelete(sql = "UPDATE movimientos_inventario SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @ToString.Exclude
    private Producto producto;

    @Column(name = "usuario_id")
    private UUID usuarioId;

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
    // private UUID referenciaId;

    @Column(name = "referencia")
    private String referencia;

    @Column(nullable = false)
    private Instant fecha = Instant.now();
}
