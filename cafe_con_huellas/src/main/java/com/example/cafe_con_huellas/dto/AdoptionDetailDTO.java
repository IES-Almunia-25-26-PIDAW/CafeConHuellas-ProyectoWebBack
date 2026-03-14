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

/**
 * DTO para el registro y consulta de detalles post-adopción.
 * <p>
 * Transfiere la información técnica y de seguimiento de una adopción
 * entre el controlador y el servicio. La fecha no puede ser futura
 * y los campos de texto tienen límite de 2000 caracteres.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionDetailDTO {

    /** Identificador único del registro. Nulo en creaciones. */
    private Long id;

    /** Identificador del vínculo usuario-mascota al que pertenece este detalle. */
    @NotNull(message = "El ID de la relación es obligatorio")
    private Long userPetRelationshipId;

    /** Fecha en la que se formalizó la adopción. No puede ser futura. */
    @NotNull(message = "La fecha de adopción es obligatoria")
    @PastOrPresent(message = "La fecha de adopción no puede ser futura")
    private LocalDate adoptionDate;

    /** Lugar donde se realizó la entrega de la mascota. Máximo 255 caracteres. */
    @NotBlank(message = "El lugar de la adopción es obligatorio")
    @Size(max = 255, message = "El lugar no puede exceder los 255 caracteres")
    private String place;

    /** Descripción de las condiciones del animal en el momento de la adopción. */
    @Size(max = 2000, message = "Las condiciones son demasiado extensas")
    private String conditions;

    /** Incidencias o problemas detectados durante el proceso. */
    @Size(max = 2000, message = "El registro de incidencias es demasiado extenso")
    private String issues;

    /** Notas adicionales de seguimiento. */
    @Size(max = 2000, message = "Las notas son demasiado extensas")
    private String notes;
}
