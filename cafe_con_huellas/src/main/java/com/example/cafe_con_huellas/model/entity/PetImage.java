package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una foto individual dentro de la galería de una mascota.
 * <p>
 * Permite asociar múltiples imágenes a un mismo animal.
 * Mapea a la tabla {@code Pet_Image}.
 * </p>
 */
@Entity
@Table(name = "Pet_Image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetImage {

    /** Identificador único autoincremental de la imagen. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación: mascota a la que pertenece esta imagen.
     * Muchas imágenes pueden pertenecer a una misma mascota.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    /** URL donde está almacenada la imagen. */
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

}
