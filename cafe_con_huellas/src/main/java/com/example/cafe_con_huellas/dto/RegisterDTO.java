package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO exclusivo para el registro de nuevos usuarios
// Separado de UserDetailDTO para que la contraseña nunca viaje en las respuestas
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(min = 2, max = 50)
    private String lastName1;

    // Segundo apellido opcional
    @Size(max = 50)
    private String lastName2;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email no válido")
    private String email;

    // Mínimo 8 caracteres, se encriptará antes de guardar en BD
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String phone;

    // Si no se especifica, el servicio asignará USER por defecto
    private String role;

    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen debe ser una URL válida")
    private String imageUrl;
}