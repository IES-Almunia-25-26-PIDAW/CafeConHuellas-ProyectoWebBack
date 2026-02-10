package com.example.cafe_con_huellas.dto;

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
    private Long petId;
    private Long vaccineId;
    private LocalDate dateAdministered;
    private LocalDate nextDoseDate;
    private String notes;
}
