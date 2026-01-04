package com.diegoperalta.pos.modules.iam.application.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
