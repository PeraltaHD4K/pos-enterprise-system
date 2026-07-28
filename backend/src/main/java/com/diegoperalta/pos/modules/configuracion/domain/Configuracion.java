package com.diegoperalta.pos.modules.configuracion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.diegoperalta.pos.common.domain.AuditableEntity;
import org.hibernate.annotations.SQLDelete;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

@Data
@EqualsAndHashCode(callSuper = false)
@SQLDelete(sql = "UPDATE configuracion SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Entity
@Table(name = "configuracion")
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion extends AuditableEntity {
    @Id
    @Column(length = 50)
    private String clave;

    @Column(length = 255)
    private String valor;
}
