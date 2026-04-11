package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro y consulta de una imagen individual de la galería de una mascota.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetImageDTO {

    /** Identificador único de la imagen. Nulo en creaciones. */
    private Long id;

    /** Identificador de la mascota a la que pertenece esta imagen. */
    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    /** URL donde está almacenada la imagen. Debe ser una URL válida. */
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La URL de la imagen debe ser válida")
    private String imageUrl;
}
