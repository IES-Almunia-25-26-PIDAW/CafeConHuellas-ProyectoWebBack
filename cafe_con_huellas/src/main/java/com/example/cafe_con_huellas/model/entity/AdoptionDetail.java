package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Define que esta clase es una entidad de persistencia y mapea a la tabla "Adoption_Detail"
@Entity
@Table(name = "Adoption_Detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionDetail {

    // Identificador único autoincremental para cada registro de adopción
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación 1:1 obligatoria que vincula el detalle con la relación usuario-mascota
    @OneToOne(optional = false)
    @JoinColumn(name = "user_pet_relationship_id")
    private UserPetRelationship relationship;

    @Column(name = "adoption_date", nullable = false)
    private LocalDate adoptionDate;

    @Column(nullable = false)
    private String place;

    // Se usa TEXT para permitir descripciones extensas sin límite corto de caracteres
    @Column(columnDefinition = "TEXT")
    private String conditions;

    @Column(columnDefinition = "TEXT")
    private String issues;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
