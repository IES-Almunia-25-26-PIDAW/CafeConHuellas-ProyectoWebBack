package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO resumido para el listado de usuarios en el panel de administración.
 * <p>
 * Contiene solo los campos esenciales para identificar a cada usuario
 * en la vista de listado, sin exponer información sensible.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDTO {

    /** Identificador único del usuario. */
    private Long id;

    /** Nombre de pila del usuario. Entre 2 y 50 caracteres. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    /** Primer apellido del usuario. Entre 2 y 50 caracteres. */
    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName1;

    /** Email del usuario. Único en el sistema. */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    /** Rol del usuario en el sistema (ADMIN o USER). */
    @NotBlank(message = "El rol es obligatorio")
    private String role;

    /** URL de la foto de perfil del usuario. Debe ser una URL válida. Opcional. */
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen de perfil debe ser una URL válida")
    private String imageUrl;
}
