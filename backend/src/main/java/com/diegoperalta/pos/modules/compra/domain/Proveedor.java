package com.diegoperalta.pos.modules.compra.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@SQLDelete(sql = "UPDATE proveedores SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Entity
@Table(name = "proveedores")
public class Proveedor extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String empresa;

    private String contacto;

    private String telefono;

    private String email;

    @Column(name = "dia_visita")
    private String diaVisita;

    private boolean activo = true;
}