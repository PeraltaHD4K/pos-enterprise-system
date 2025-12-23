package com.diegoperalta.pos.modules.inventario.domain;

import lombok.Data;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String sku;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "precio_venta", nullable = false)
    private BigDecimal precioVenta;

    @Column(name = "costo_promedio")
    private BigDecimal costoPromedio;

    @Column(name = "ultimo_costo_compra")
    private BigDecimal ultimoCostoCompra;

    @Column(name = "stock_actual")
    private Integer stockActual;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    private Boolean activo = true;

    // RELACIÓN: Un producto pertenece a UNA categoría.
    // FetchType.EAGER: Cuando cargues el producto, trae también los datos de la
    // categoría.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
