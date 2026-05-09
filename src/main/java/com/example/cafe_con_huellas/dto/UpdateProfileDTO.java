package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que un usuario autenticado actualice su propio perfil.
 * <p>
 * Solo contiene los campos que un usuario normal puede modificar.
 * No incluye email (identificador de autenticación), rol, ni contraseña,
 * ya que esos campos tienen sus propios flujos de modificación
 * o están reservados al administrador.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileDTO {

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

    /** Teléfono de contacto. Formato internacional: entre 7 y 15 dígitos, con prefijo opcional (+). */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos numéricos, con prefijo internacional opcional (+)")
    private String phone;

    /** URL de la foto de perfil. Debe ser una URL válida. Opcional. */
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen debe ser una URL válida")
    private String imageUrl;
}