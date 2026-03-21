package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que se devuelve al frontend tras un login exitoso o una renovación de token.
 * <p>
 * Contiene únicamente los dos tokens JWT. Los datos del usuario (email y rol)
 * viajan de forma segura <b>dentro</b> del access token como claims firmados,
 * no expuestos directamente en la respuesta.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    /**
     * Access token JWT de corta duración (15 minutos).
     * <p>
     * El frontend debe enviarlo en cada petición autenticada mediante el header:
     * {@code Authorization: Bearer <token>}
     * Contiene el email como subject y el rol como claim interno.
     * </p>
     */
    private String token;

    /**
     * Refresh token JWT de larga duración (7 días).
     * <p>
     * El frontend lo usa <b>exclusivamente</b> para solicitar un nuevo access token
     * cuando este expira, llamando a {@code POST /api/auth/refresh}.
     * Nunca debe enviarse en peticiones normales de la aplicación.
     * </p>
     */
    private String refreshToken;
}
