package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// DTO de vínculo usuario-mascota
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetRelationshipDTO {

    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    @NotBlank(message = "El tipo de relación (ADOPCION, ACOGIDA, etc.) es obligatorio")
    private String relationshipType;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede ser futura")
    private LocalDate startDate;

    // La fecha de fin puede ser nula si la relación sigue activa (adopción permanente)
    @FutureOrPresent(message = "La fecha de fin debe ser hoy o una fecha futura")
    private LocalDate endDate;

    @NotNull(message = "Debes indicar si el vínculo está activo")
    private Boolean active;
}
