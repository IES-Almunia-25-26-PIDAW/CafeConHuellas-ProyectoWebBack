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

/*
 * Esta clase centraliza el manejo de excepciones de todos los controladores.
 * Con @RestControllerAdvice, Spring "escucha" si ocurre un error en cualquier API
 * y lo redirige aquí para darle un formato bonito al usuario.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 - Petición incorrecta
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /*
     * 400 - Errores de validación (@Valid en los DTOs).
     * Este caso es especial porque necesitamos extraer la lista de errores
     * de cada campo que falló la validación, por eso no usa buildResponse().
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

    // 500 - Cualquier otro error no controlado cae aquí como red de seguridad
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    /*
     * 403 - Acceso denegado.
     * Se dispara cuando un usuario autenticado intenta acceder a un recurso
     * para el que no tiene permisos (p. ej. un USER accediendo a rutas de ADMIN).
     */
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(
            org.springframework.security.authorization.AuthorizationDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado");
    }

    /*
     * 401 - Credenciales incorrectas.
     * Se dispara cuando el usuario envía un usuario o contraseña equivocados
     * durante el proceso de login.
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

    /*
     * Método auxiliar para construir la respuesta JSON de error de forma uniforme.
     * Todos los handlers (excepto el de validación) delegan aquí para garantizar
     * que el formato de la respuesta sea siempre el mismo:
     *   {
     *     "timestamp": "...",
     *     "message": "..."
     *   }
     *
     * IMPORTANTE: devuelve Map<String, Object> (no Object) para que sea compatible
     * con el tipo de retorno declarado en todos los @ExceptionHandler de esta clase.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now()); // Fecha y hora exacta del error
        body.put("message", message);               // Mensaje descriptivo del error
        return new ResponseEntity<>(body, status);
    }
}