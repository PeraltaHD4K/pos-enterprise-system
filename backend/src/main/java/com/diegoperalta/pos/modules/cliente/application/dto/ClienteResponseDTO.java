package com.diegoperalta.pos.modules.cliente.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {
    private UUID id;
    private String nombre;
    private String telefono;
    private String email;
    private Integer puntosFidelidad;
}
