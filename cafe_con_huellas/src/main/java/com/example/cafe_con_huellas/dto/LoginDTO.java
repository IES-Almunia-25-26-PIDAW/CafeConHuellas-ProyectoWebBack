package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO que recibe el frontend cuando el usuario quiere hacer login
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {

    // Email como identificador único del usuario
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email no válido")
    private String email;

    // Contraseña en texto plano, se comparará con el hash de la BD
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}