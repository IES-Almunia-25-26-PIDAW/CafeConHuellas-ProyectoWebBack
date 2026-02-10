package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para vacunas disponibles
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineDTO {
    private Long id;
    private String name;
    private String description;
}
