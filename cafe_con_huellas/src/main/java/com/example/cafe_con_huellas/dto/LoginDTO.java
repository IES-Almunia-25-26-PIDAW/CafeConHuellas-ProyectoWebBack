package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que recibe las credenciales del usuario para el proceso de login.
 * <p>
 * La contraseña viaja en texto plano y se compara contra el hash BCrypt
 * almacenado en la base de datos. Nunca se devuelve en las respuestas.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {

    /** Email del usuario, usado como identificador único de autenticación. */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email no válido")
    private String email;

    /** Contraseña en texto plano del usuario. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}