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

// DTO para mostrar historial de vacunas
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetVaccineDTO {

    private Long id;

    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    @NotNull(message = "El ID de la vacuna es obligatorio")
    private Long vaccineId;

    @NotNull(message = "La fecha de administración es obligatoria")
    @PastOrPresent(message = "La fecha de administración no puede ser futura")
    private LocalDate dateAdministered;

    // La próxima dosis siempre debería ser hoy o en el futuro
    @FutureOrPresent(message = "La fecha de la próxima dosis debe ser hoy o una fecha futura")
    private LocalDate nextDoseDate;

    @Size(max = 500, message = "Las notas médicas no pueden superar los 500 caracteres")
    private String notes;
}
