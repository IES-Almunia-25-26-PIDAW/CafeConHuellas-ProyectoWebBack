package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO completo para detalles de usuario
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName1;

    // El segundo apellido es opcional, pero si se pone, validamos su tamaño
    @Size(max = 50, message = "El segundo apellido no puede exceder los 50 caracteres")
    private String lastName2;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos numéricos")
    private String phone;

    @NotBlank(message = "El rol de usuario es obligatorio")
    private String role; // El Mapper lo convertirá al Enum UserRole

    // Imagen de perfil del usuario (opcional)
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen de perfil debe ser una URL válida")
    private String imageUrl;
}
