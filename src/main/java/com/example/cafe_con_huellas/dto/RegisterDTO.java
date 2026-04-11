package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO exclusivo para el registro de nuevos usuarios.
 * <p>
 * Separado de {@link UserDetailDTO} para que la contraseña nunca
 * viaje en las respuestas de la API. La contraseña se encripta
 * con BCrypt antes de persistirse en la base de datos.
 * Si no se especifica rol, el servicio asigna {@code USER} por defecto.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {

    /** Nombre de pila del usuario. Entre 2 y 50 caracteres. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50)
    private String firstName;

    /** Primer apellido del usuario. Entre 2 y 50 caracteres. */
    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(min = 2, max = 50)
    private String lastName1;

    /** Segundo apellido del usuario. Opcional. Máximo 50 caracteres. */
    @Size(max = 50)
    private String lastName2;

    /** Email del usuario. Único en el sistema. */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email no válido")
    private String email;

    /** Contraseña en texto plano. Mínimo 8 caracteres. Se encriptará antes de guardarse en la base de datos. */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    /** Teléfono de contacto. Debe tener exactamente 9 dígitos numéricos. */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String phone;

    /** Rol del usuario (ADMIN o USER). Si no se especifica, se asigna USER por defecto. */
    private String role;

    /** URL de la foto de perfil del usuario. Debe ser una URL válida. Opcional. */
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen debe ser una URL válida")
    private String imageUrl;
}