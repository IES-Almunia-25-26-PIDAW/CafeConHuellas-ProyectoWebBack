package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO básico para listar mascotas
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetSummaryDTO {
    private Long id;
    private String name;
    private String breed;
    private String category;
    private Integer age;
    private Double weight;
    private Boolean neutered;
    private Boolean isPpp;
    private String imageUrl;
}
