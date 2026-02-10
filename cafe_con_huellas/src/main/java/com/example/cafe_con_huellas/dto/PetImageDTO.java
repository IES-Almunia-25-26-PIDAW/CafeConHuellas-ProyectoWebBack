package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO simple de imágenes de mascota
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetImageDTO {
    private Long id;
    private Long petId; // referencia a la mascota
    private String imageUrl;
}
