package com.example.cafe_con_huellas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para verificar el estado de la API.
 * <p>
 * Endpoint público que permite comprobar rápidamente
 * que el backend está levantado y respondiendo correctamente.
 * </p>
 */
@RestController
public class HealthCheckController {

    /**
     * Comprueba que la API está operativa.
     *
     * @return texto "Ok" si el servidor responde correctamente
     */
    @GetMapping("/api/health-check")
    public String healthCheck() {
        return "Ok";
    }
}