package com.diegoperalta.pos.common.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> manejarBusinessException(BusinessException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", Instant.now());
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("estado", ex.getEstado().value());
        respuesta.put("error", ex.getEstado().getReasonPhrase());

        return new ResponseEntity<>(respuesta, ex.getEstado());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex) {
        ex.printStackTrace();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", Instant.now());
        respuesta.put("mensaje", "Ocurrió un error interno en el servidor");
        respuesta.put("detalle", ex.getMessage()); // Quitar en producción
        respuesta.put("estado", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
