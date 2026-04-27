package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entidad que representa a una mascota disponible en el refugio.
 * <p>
 * Almacena la información descriptiva, médica y visual del animal.
 * Incluye una relación con su galería de imágenes adicionales.
 * Mapea a la tabla {@code Pet}.
 * </p>
 */
@Entity
@Table(name = "Pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    /** Identificador único autoincremental de la mascota. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de la mascota. */
    @Column(nullable = false)
    private String name;

    /** Descripción de la personalidad, historia o características del animal. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Raza de la mascota. */
    @Column(nullable = false)
    private String breed;

    /** Categoría del animal (PERRO o GATO). Almacenada como texto en base de datos. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetCategory category;;

    /** Edad de la mascota en años. */
    @Column(nullable = false)
    private Integer age;

    /** Peso de la mascota en kilogramos. */
    @Column(nullable = false)
    private BigDecimal weight;

    /** Indica si la mascota está esterilizada. */
    @Column(nullable = false)
    private Boolean neutered;

    /** Indica si la mascota está clasificada como Perro Potencialmente Peligroso (PPP). */
    @Column(name = "is_ppp", nullable = false)
    private Boolean isPpp = false;

    /** Indica si la adopción de esta mascota es urgente. */
    @Column(name = "urgent_adoption", nullable = false)
    private Boolean urgentAdoption = false;

    /** URL de la imagen principal de la mascota. */
    @Column(name = "image_url")
    private String imageUrl;

    /** Estado del proceso de adopción de la mascota. */
    @Enumerated(EnumType.STRING)
    @Column(name = "adoption_status", nullable = false)
    private AdoptionStatus adoptionStatus;


    /**
     * Relación con la galería de imágenes adicionales de la mascota.
     * <p>
     * Se carga de forma lazy para optimizar el rendimiento.
     * Al eliminar la mascota, todas sus imágenes se eliminan en cascada.
     * orphanRemoval: si quitas una foto de la lista, se elimina de la BD.
     * </p>
     */
    @OneToMany(
            mappedBy = "pet",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PetImage> images;



}
