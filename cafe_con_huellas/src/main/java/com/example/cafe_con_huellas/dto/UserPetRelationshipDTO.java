package com.example.cafe_con_huellas.dto;

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
    private Long userId;
    private Long petId;
    private String relationshipType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
}
