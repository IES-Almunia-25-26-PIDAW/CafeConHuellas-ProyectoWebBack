package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para vacunas disponibles
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineDTO {

    private Long id;

    @NotBlank(message = "El nombre de la vacuna es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de la vacuna debe tener entre 3 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción de la vacuna es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener al menos 10 caracteres")
    private String description;
}
