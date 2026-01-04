package com.diegoperalta.pos.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus estado;

    public BusinessException(String mensaje, HttpStatus estado) {
        super(mensaje);
        this.estado = estado;
    }

    public BusinessException(String mensaje) {
        super(mensaje);
        this.estado = HttpStatus.BAD_REQUEST;
    }
}
