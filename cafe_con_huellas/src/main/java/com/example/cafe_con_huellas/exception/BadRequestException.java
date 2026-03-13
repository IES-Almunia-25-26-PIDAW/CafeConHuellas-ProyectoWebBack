package com.example.cafe_con_huellas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para peticiones incorrectas o datos de entrada no válidos.
 * <p>
 * Se lanza cuando el cliente envía datos que no cumplen las reglas de negocio,
 * como duplicados, valores fuera de rango o combinaciones no permitidas.
 * Devuelve automáticamente el código HTTP {@code 400 Bad Request}.
 * </p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Crea una nueva excepción con el mensaje descriptivo del error.
     *
     * @param message descripción del motivo por el que la petición es inválida
     */
    public BadRequestException(String message) {
        super(message);
    }
}