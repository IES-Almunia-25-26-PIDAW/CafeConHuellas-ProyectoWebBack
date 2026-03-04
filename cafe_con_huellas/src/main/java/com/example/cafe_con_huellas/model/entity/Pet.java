package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// Representa a una mascota disponible en el sistema
@Entity
@Table(name = "Pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Uso de TEXT para descripciones largas de la personalidad o historia de la mascota
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Raza de la mascota
    @Column(nullable = false)
    private String breed;

    // Ejemplo: PERRO, GATO
    // Usamos STRING para que en la BD se guarde el texto y no la posición numérica
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetCategory category;;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private BigDecimal weight;

    // Indica si está esterilizado (true/false)
    @Column(nullable = false)
    private Boolean neutered;

    // Indica si es Perro Potencialmente Peligroso
    @Column(name = "is_ppp", nullable = false)
    private Boolean isPpp = false;

    @Column(name = "image_url")
    private String imageUrl;


    /* Relación con la galería de fotos extra:
       - cascade: si borras la mascota, se borran sus fotos.
       - orphanRemoval: si quitas una foto de la lista, se elimina de la BD.
       - fetch = LAZY: las fotos solo se cargan si realmente se necesitan.
    */
    @OneToMany(
            mappedBy = "pet",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PetImage> images;



}
