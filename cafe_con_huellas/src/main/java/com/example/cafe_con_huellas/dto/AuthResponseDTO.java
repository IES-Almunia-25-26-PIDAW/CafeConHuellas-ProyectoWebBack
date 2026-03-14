package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que se devuelve al frontend tras un login exitoso.
 * <p>
 * Contiene el token JWT que el cliente debe almacenar y enviar
 * en el header {@code Authorization} de cada petición posterior,
 * junto con los datos básicos del usuario autenticado.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    /** Token JWT que el frontend debe incluir en cada petición como {@code Bearer <token>}. */
    private String token;

    /** Email del usuario autenticado. (útil para el frontend) */
    private String email;

    /** Rol del usuario (ADMIN o USER) para que el frontend gestione la visibilidad de opciones. */
    private String role;
}
