package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un tipo de vacuna disponible en el catálogo del sistema.
 * <p>
 * Define los tipos de vacunas que pueden asignarse a las mascotas
 * a través de {@link PetVaccine}. El nombre de cada vacuna es único.
 * Mapea a la tabla {@code Vaccine}.
 * </p>
 */
@Entity
@Table(name = "Vaccine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vaccine {

    /** Identificador único autoincremental de la vacuna. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de la vacuna. Único en el catálogo. */
    @Column(nullable = false)
    private String name;

    /** Descripción de la vacuna, indicaciones o enfermedades que previene. */
    @Column(columnDefinition = "TEXT")
    private String description;



}
