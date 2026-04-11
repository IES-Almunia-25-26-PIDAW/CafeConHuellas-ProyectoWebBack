package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para el registro y consulta del historial de vacunación de una mascota.
 * <p>
 * La fecha de administración no puede ser futura y la fecha de la próxima
 * dosis debe ser hoy o una fecha posterior.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetVaccineDTO {

    /** Identificador único del registro de vacunación. Nulo en creaciones. */
    private Long id;

    /** Identificador de la mascota que recibió la vacuna. */
    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    /** Identificador del tipo de vacuna aplicada. */
    @NotNull(message = "El ID de la vacuna es obligatorio")
    private Long vaccineId;

    /** Fecha en la que se administró la vacuna. No puede ser futura. */
    @NotNull(message = "La fecha de administración es obligatoria")
    @PastOrPresent(message = "La fecha de administración no puede ser futura")
    private LocalDate dateAdministered;

    /** Fecha sugerida para el refuerzo o siguiente dosis. Debe ser hoy o futura. Opcional. */
    @FutureOrPresent(message = "La fecha de la próxima dosis debe ser hoy o una fecha futura")
    private LocalDate nextDoseDate;

    /** Notas clínicas adicionales (lote, reacciones, etc.). Máximo 500 caracteres. */
    @Size(max = 500, message = "Las notas médicas no pueden superar los 500 caracteres")
    private String notes;
}
