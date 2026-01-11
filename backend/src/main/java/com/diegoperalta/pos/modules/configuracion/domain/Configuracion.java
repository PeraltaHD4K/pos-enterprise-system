package com.diegoperalta.pos.modules.configuracion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "configuracion")
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion {
    @Id
    @Column(length = 50)
    private String clave;

    @Column(length = 255)
    private String valor;
}
