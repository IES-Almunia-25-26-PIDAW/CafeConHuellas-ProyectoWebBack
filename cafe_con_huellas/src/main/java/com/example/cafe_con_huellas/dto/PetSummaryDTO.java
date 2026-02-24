package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO básico para listar mascotas
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetSummaryDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank(message = "La raza es obligatoria")
    private String breed;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 20, message = "Introduce una edad realista (máximo 20)")
    private Integer age;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.1", message = "El peso debe ser al menos de 0.1 kg")
    @DecimalMax(value = "80.0", message = "El peso no puede superar los 80 kg")
    private Double weight;

    @NotNull(message = "Indica si está esterilizado")
    private Boolean neutered;

    @NotNull(message = "Indica si es PPP")
    private Boolean isPpp;

    @NotBlank(message = "La imagen principal es obligatoria")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "La URL de la imagen debe ser válida")
    private String imageUrl;
}
