package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Representa una foto individual dentro de la galería de una mascota
@Entity
@Table(name = "Pet_Image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación que vincula esta imagen a una mascota específica
    // Muchas imágenes pueden pertenecer a una misma mascota
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // Dirección (link) donde está almacenada la imagen
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

}
