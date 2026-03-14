package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO resumido para el listado del catálogo de mascotas.
 * <p>
 * Contiene solo los campos esenciales para mostrar la tarjeta de cada mascota
 * en la galería principal, evitando cargar información innecesaria.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetSummaryDTO {

    /** Identificador único de la mascota. */
    private Long id;

    /** Nombre de la mascota. Entre 2 y 50 caracteres. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    /** Raza de la mascota. */
    @NotBlank(message = "La raza es obligatoria")
    private String breed;

    /** Categoría del animal (PERRO o GATO). */
    @NotBlank(message = "La categoría es obligatoria")
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
    private Double weight;

    /** Indica si la mascota está esterilizada. */
    @NotNull(message = "Indica si está esterilizado")
    private Boolean neutered;

    /** Indica si la mascota está clasificada como Perro Potencialmente Peligroso (PPP). */
    @NotNull(message = "Indica si es PPP")
    private Boolean isPpp;

    /** URL de la imagen principal de la mascota. */
    @NotBlank(message = "La imagen principal es obligatoria")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La URL de la imagen debe ser válida")
    private String imageUrl;
}
