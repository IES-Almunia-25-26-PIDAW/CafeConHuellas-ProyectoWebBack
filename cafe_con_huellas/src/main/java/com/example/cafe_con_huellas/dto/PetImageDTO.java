package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO simple de imágenes de mascota
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetImageDTO {

    private Long id;

    // Referencia a la mascota
    // Obligatorio para saber a qué mascota pertenece la foto
    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    // La URL de la imagen individual
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La URL de la imagen debe ser válida")
    private String imageUrl;
}
