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
 * DTO completo para la consulta y actualización del perfil de un usuario.
 * <p>
 * No incluye la contraseña, ya que este DTO se usa únicamente en respuestas
 * y actualizaciones de perfil. Para el registro usar {@link RegisterDTO}.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailDTO {

    /** Identificador único del usuario. Nulo en creaciones. */
    private Long id;

    /** Nombre de pila del usuario. Entre 2 y 50 caracteres. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    /** Primer apellido del usuario. Entre 2 y 50 caracteres. */
    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName1;

    /** Segundo apellido del usuario. Opcional. Máximo 50 caracteres. */
    @Size(max = 50, message = "El segundo apellido no puede exceder los 50 caracteres")
    private String lastName2;

    /** Email del usuario. Único en el sistema. */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    /** Teléfono de contacto. Formato internacional: entre 7 y 15 dígitos, con prefijo opcional (+). */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos numéricos, con prefijo internacional opcional (+)")
    private String phone;

    /** Rol del usuario en el sistema (ADMIN o USER). El mapper lo convierte al enum correspondiente. */
    @NotBlank(message = "El rol de usuario es obligatorio")
    private String role; // El Mapper lo convertirá al Enum UserRole

    /** URL de la foto de perfil del usuario. Debe ser una URL válida. Opcional. */
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen de perfil debe ser una URL válida")
    private String imageUrl;
}
