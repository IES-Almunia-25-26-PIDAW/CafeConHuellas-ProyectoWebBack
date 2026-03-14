package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro y consulta de vacunas del catálogo.
 * <p>
 * Define los tipos de vacunas disponibles en el sistema
 * para poder asignarlas posteriormente a las mascotas.
 * El nombre debe ser único en el catálogo.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineDTO {

    /** Identificador único de la vacuna. Nulo en creaciones. */
    private Long id;

    /** Nombre de la vacuna. Único en el catálogo. Entre 3 y 100 caracteres. */
    @NotBlank(message = "El nombre de la vacuna es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de la vacuna debe tener entre 3 y 100 caracteres")
    private String name;

    /** Descripción de la vacuna, indicaciones o enfermedades que previene. Mínimo 10 caracteres. */
    @NotBlank(message = "La descripción de la vacuna es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener al menos 10 caracteres")
    private String description;
}
