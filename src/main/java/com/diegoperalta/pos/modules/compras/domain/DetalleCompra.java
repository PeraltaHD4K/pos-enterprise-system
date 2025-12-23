package com.diegoperalta.pos.modules.compras.domain;

import com.diegoperalta.pos.modules.inventario.domain.Producto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalle_compras")
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    @JsonIgnore
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

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
