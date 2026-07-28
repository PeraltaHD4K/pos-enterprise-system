package com.diegoperalta.pos.modules.iam.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolResponseDTO {
    private UUID id;
    private String nombre;
}
