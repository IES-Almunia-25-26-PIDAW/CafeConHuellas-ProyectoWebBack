package com.example.cafe_con_huellas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/* Excepción para peticiones incorrectas o datos inválidos.
 * Automáticamente devuelve un código de estado 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}