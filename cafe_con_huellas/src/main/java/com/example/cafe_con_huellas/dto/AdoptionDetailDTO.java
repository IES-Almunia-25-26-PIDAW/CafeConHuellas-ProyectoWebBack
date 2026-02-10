package com.example.cafe_con_huellas.dto;

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
    private Long userPetRelationshipId;
    private LocalDate adoptionDate;
    private String place;
    private String conditions;
    private String issues;
    private String notes;
}
