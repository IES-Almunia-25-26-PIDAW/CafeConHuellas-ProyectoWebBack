package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO completo para detalle de mascota
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDetailDTO {
    private Long id;
    private String name;
    private String description;
    private String breed;
    private String category;
    private Integer age;
    private Double weight;
    private Boolean neutered;
    private Boolean isPpp;
    private String imageUrl;
    private List<String> imageUrls; // Lista de URLs de las imágenes adicionales
}
