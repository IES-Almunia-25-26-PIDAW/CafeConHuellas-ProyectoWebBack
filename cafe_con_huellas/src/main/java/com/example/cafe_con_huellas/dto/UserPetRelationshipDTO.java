package com.example.cafe_con_huellas.dto;

import com.example.cafe_con_huellas.model.entity.RelationshipType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para el registro y consulta de vínculos entre usuarios y mascotas.
 * <p>
 * La fecha de inicio no puede ser futura y la fecha de fin,
 * si se proporciona, debe ser hoy o posterior.
 * El tipo de relación debe coincidir con los valores del enum {@link RelationshipType}.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetRelationshipDTO {

    /** Identificador único del vínculo. Nulo en creaciones. */
    private Long id;

    /** Identificador del usuario que participa en el vínculo. */
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    /** Identificador de la mascota que forma parte del vínculo. */
    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    /** Tipo de relación (ADOPCION, ACOGIDA, PASEO, VOLUNTARIADO). */
    @NotBlank(message = "El tipo de relación (ADOPCION, ACOGIDA, etc.) es obligatorio")
    private String relationshipType;

    /** Fecha de inicio del vínculo. No puede ser futura. */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede ser futura")
    private LocalDate startDate;

    /** Fecha de finalización del vínculo. Debe ser hoy o futura. Nula si sigue activo. */
    @FutureOrPresent(message = "La fecha de fin debe ser hoy o una fecha futura")
    private LocalDate endDate;

    /** Indica si el vínculo está actualmente activo. */
    @NotNull(message = "Debes indicar si el vínculo está activo")
    private Boolean active;
}
