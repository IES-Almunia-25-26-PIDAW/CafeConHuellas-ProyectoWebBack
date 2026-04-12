package com.example.cafe_con_huellas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manejador global de excepciones para todos los controladores REST.
 * <p>
 * Centraliza el tratamiento de errores mediante {@code @RestControllerAdvice},
 * interceptando las excepciones lanzadas en cualquier punto de la aplicación
 * y devolviendo siempre una respuesta JSON uniforme con el timestamp y el mensaje.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja los errores de recurso no encontrado.
     * Devuelve HTTP {@code 404 Not Found}.
     *
     * @param ex excepción capturada con el mensaje del recurso no encontrado
     * @return respuesta JSON con timestamp y mensaje de error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Maneja los errores de petición incorrecta o datos inválidos.
     * Devuelve HTTP {@code 400 Bad Request}.
     *
     * @param ex excepción capturada con el mensaje del error de negocio
     * @return respuesta JSON con timestamp y mensaje de error
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }



    /**
     * Maneja los errores de archivo faltante en peticiones multipart.
     * <p>
     * Se lanza cuando el cliente envía una petición multipart sin incluir
     * el archivo obligatorio (por ejemplo, sin el campo {@code file}).
     * Devuelve HTTP {@code 400 Bad Request}.
     * </p>
     *
     * @param ex excepción con el nombre del campo que falta
     * @return respuesta JSON con timestamp y mensaje descriptivo
     */
    @ExceptionHandler(org.springframework.web.multipart.support.MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, Object>> handleMissingPart(
            org.springframework.web.multipart.support.MissingServletRequestPartException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Falta el archivo en la petición: " + ex.getRequestPartName());
    }


    /**
     * Maneja los errores de validación de los DTOs anotados con {@code @Valid}.
     * <p>
     * Extrae el mensaje de error de cada campo que no superó la validación
     * y los devuelve como una lista en la respuesta.
     * Devuelve HTTP {@code 400 Bad Request}.
     * </p>
     *
     * @param ex excepción capturada con la lista de errores de validación por campo
     * @return respuesta JSON con timestamp, mensaje genérico y lista de errores de campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Recorremos todos los errores de campo y extraemos el mensaje de cada uno
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Error de validación");
        body.put("errors", errors); // Lista con todos los mensajes de validación fallidos

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier excepción no controlada explícitamente como red de seguridad.
     * Devuelve HTTP {@code 500 Internal Server Error}.
     *
     * @param ex excepción genérica capturada
     * @return respuesta JSON con timestamp y mensaje de error interno
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    /**
     * Maneja los errores de acceso denegado cuando un usuario autenticado
     * intenta acceder a un recurso para el que no tiene permisos.
     * Devuelve HTTP {@code 403 Forbidden}.
     *
     * @param ex excepción de autorización denegada
     * @return respuesta JSON con timestamp y mensaje de acceso denegado
     */
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(
            org.springframework.security.authorization.AuthorizationDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado");
    }

    /**
     * Maneja los errores de credenciales incorrectas durante el proceso de login.
     * Devuelve HTTP {@code 401 Unauthorized}.
     *
     * @param ex excepción de credenciales inválidas
     * @return respuesta JSON con timestamp y mensaje de credenciales incorrectas
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

    /**
     * Método auxiliar que construye la respuesta JSON de error de forma uniforme.
     * <p>
     * Todos los manejadores excepto el de validación delegan aquí para garantizar
     * que el formato de respuesta sea siempre consistente:
     * </p>
     * <pre>
     * {
     *   "timestamp": "...",
     *   "message": "..."
     * }
     * </pre>
     *
     * @param status  código HTTP que se devolverá en la respuesta
     * @param message mensaje descriptivo del error
     * @return {@link ResponseEntity} con el cuerpo JSON y el código de estado indicado
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now()); // Fecha y hora exacta del error
        body.put("message", message);               // Mensaje descriptivo del error
        return new ResponseEntity<>(body, status);
    }
}