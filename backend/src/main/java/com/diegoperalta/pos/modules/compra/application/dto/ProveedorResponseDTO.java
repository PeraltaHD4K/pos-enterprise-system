package com.diegoperalta.pos.modules.compra.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponseDTO {
    private UUID id;
    private String empresa;
    private String contacto;
    private String telefono;
    private String email;
    private String diaVisita;
    private boolean activo;
}
