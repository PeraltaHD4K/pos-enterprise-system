package com.diegoperalta.pos.modules.compra.domain;

import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import java.math.BigDecimal;
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
@SQLDelete(sql = "UPDATE detalle_compras SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Entity
@Table(name = "detalle_compras")
public class DetalleCompra extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Compra compra;

    @Column(name = "producto_id")
    private UUID productoId;

    @Column(name = "cantidad_pedida", nullable = false)
    private Integer cantidadPedida;

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida;

    @Column(name = "unidades_por_caja")
    private Integer unidadesPorCaja = 1;

    @Column(name = "costo_total_renglon")
    private BigDecimal costoTotalRenglon;

    @Column(name = "costo_unitario_calculado", precision = 10, scale = 4)
    private BigDecimal costoUnitarioCalculado;

    public Integer getTotalPiezasReales() {
        int recibida = (cantidadRecibida != null) ? cantidadRecibida : 0;
        int factor = (unidadesPorCaja != null && unidadesPorCaja > 0) ? unidadesPorCaja : 1;
        return recibida * factor;
    }
}
