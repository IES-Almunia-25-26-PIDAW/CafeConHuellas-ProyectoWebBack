package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO completo para detalle de mascota
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDetailDTO {

    private Long id;

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, message = "La descripción debe ser detallada (mínimo 10 caracteres)")
    private String description;

    @NotBlank(message = "La raza es obligatoria")
    private String breed;

    @NotBlank(message = "La categoría (PERRO, GATO, etc.) es obligatoria")
    private String category;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 20, message = "Introduce una edad realista (máximo 20)")
    private Integer age;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.1", message = "El peso debe ser al menos de 0.1 kg")
    @DecimalMax(value = "80.0", message = "El peso no puede superar los 80 kg")
    private Double weight;

    @NotNull(message = "Indica si la mascota está esterilizada")
    private Boolean neutered;

    @NotNull(message = "Indica si es una raza potencialmente peligrosa (PPP)")
    private Boolean isPpp;

    // Imagen principal (portada)
    @NotBlank(message = "La imagen principal de la mascota es obligatoria")
   @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen principal debe ser una URL válida")
    private String imageUrl;

    // Lista de URLs de las imágenes adicionales
    // Validación para la galería de imágenes adicionales
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "Cada imagen de la galería debe ser una URL válida")
    private List<@NotBlank(message = "La URL de la imagen no puede estar vacía") String> imageUrls;

}
