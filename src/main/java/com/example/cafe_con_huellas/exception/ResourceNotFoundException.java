package com.example.cafe_con_huellas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para recursos que no existen en el sistema.
 * <p>
 * Se lanza cuando se intenta acceder, actualizar o eliminar una entidad
 * que no se encuentra en la base de datos.
 * Devuelve automáticamente el código HTTP {@code 404 Not Found}.
 * </p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea una nueva excepción con el mensaje descriptivo del recurso no encontrado.
     *
     * @param message descripción del recurso que no se ha podido localizar
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}