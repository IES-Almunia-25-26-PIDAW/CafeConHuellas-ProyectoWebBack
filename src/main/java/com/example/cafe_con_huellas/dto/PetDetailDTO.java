package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO completo para el registro y consulta de una mascota.
 * <p>
 * Incluye todos los campos descriptivos, médicos y visuales del animal,
 * así como la galería de imágenes adicionales. Se usa tanto para
 * crear y editar mascotas como para mostrar su ficha completa.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDetailDTO {

    /** Identificador único de la mascota. Nulo en creaciones. */
    private Long id;

    /** Identificador único de la mascota. Nulo en creaciones. */
    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    /** Descripción de la personalidad o historia del animal. Mínimo 10 caracteres. */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, message = "La descripción debe ser detallada (mínimo 10 caracteres)")
    private String description;

    /** Raza de la mascota. */
    @NotBlank(message = "La raza es obligatoria")
    private String breed;

    /** Categoría del animal (PERRO o GATO). */
    @NotBlank(message = "La categoría (PERRO, GATO, etc.) es obligatoria")
    private String category;

    /** Edad de la mascota en años. Entre 0 y 20. */
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 20, message = "Introduce una edad realista (máximo 20)")
    private Integer age;

    /** Peso de la mascota en kilogramos. Entre 0,1 y 80 kg. */
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.1", message = "El peso debe ser al menos de 0.1 kg")
    @DecimalMax(value = "80.0", message = "El peso no puede superar los 80 kg")
    private BigDecimal weight;

    /** Indica si la mascota está esterilizada. */
    @NotNull(message = "Indica si la mascota está esterilizada")
    private Boolean neutered;

    /** Indica si la mascota está clasificada como Perro Potencialmente Peligroso (PPP). */
    @NotNull(message = "Indica si es una raza potencialmente peligrosa (PPP)")
    private Boolean isPpp;

    /** Indica si la adopción de esta mascota es urgente. */
    @NotNull(message = "Indica si la adopción es urgente")
    private Boolean urgentAdoption;

    /** URL de la imagen principal de la mascota. Debe ser una URL válida. (Portada) */
    @NotBlank(message = "La imagen principal de la mascota es obligatoria")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La imagen principal debe ser una URL válida")
    private String imageUrl;

    /** Lista de URLs de imágenes adicionales de la galería. Cada URL debe ser válida. */
    private List<@NotBlank(message = "La URL de la imagen no puede estar vacía")
                @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "Cada imagen de la galería debe ser una URL válida")
            String> imageUrls;

}
