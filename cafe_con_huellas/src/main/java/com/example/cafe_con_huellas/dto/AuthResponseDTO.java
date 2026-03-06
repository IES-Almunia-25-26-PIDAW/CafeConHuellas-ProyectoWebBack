package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO que devolvemos al frontend tras un login exitoso
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    // El token JWT que el frontend debe guardar y enviar en cada petición
    private String token;

    // Email del usuario autenticado (útil para el frontend)
    private String email;

    // Rol del usuario para que el frontend pueda mostrar/ocultar opciones
    private String role;
}
