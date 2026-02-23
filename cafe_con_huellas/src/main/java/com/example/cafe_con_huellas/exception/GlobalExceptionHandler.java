package com.example.cafe_con_huellas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Esta clase centraliza el manejo de excepciones de todos los controladores.
 * Con @RestControllerAdvice, Spring "escucha" si ocurre un error en cualquier API
 * y lo redirige aquí para darle un formato bonito al usuario.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Captura específicamente los errores de tipo ResourceNotFoundException (Error 404).
     * Se dispara cuando buscamos algo que no existe en la base de datos.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        // Creamos un mapa para estructurar la respuesta JSON
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now()); // Añade la fecha y hora del error
        body.put("message", ex.getMessage());        // Añade el mensaje que escribimos en el Service

        // Retornamos el mapa con el código HTTP 404
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /*
     * Captura los errores de tipo BadRequestException (Error 400).
     * Se dispara cuando los datos enviados por el usuario no son válidos.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        // Retornamos el mapa con el código HTTP 400
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}