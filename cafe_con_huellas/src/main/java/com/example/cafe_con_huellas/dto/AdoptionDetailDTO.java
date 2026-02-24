package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// DTO para detalles de adopción
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionDetailDTO {
    private Long id;

    // Obligatorio para saber a qué vínculo pertenece
    @NotNull(message = "El ID de la relación es obligatorio")
    private Long userPetRelationshipId;

    // No permitimos fechas futuras para una adopción ya realizada
    @NotNull(message = "La fecha de adopción es obligatoria")
    @PastOrPresent(message = "La fecha de adopción no puede ser futura")
    private LocalDate adoptionDate;

    @NotBlank(message = "El lugar de la adopción es obligatorio")
    @Size(max = 255, message = "El lugar no puede exceder los 255 caracteres")
    private String place;

    // Estos campos son TEXT en la BD, así que permitimos que sean largos
    @Size(max = 2000, message = "Las condiciones son demasiado extensas")
    private String conditions;

    @Size(max = 2000, message = "El registro de incidencias es demasiado extenso")
    private String issues;

    @Size(max = 2000, message = "Las notas son demasiado extensas")
    private String notes;
}
