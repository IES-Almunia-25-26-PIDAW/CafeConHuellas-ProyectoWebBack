package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad que almacena la información técnica y de seguimiento post-adopción.
 * <p>
 * Se vincula de forma 1:1 con una {@link UserPetRelationship} y recoge
 * datos como el lugar de la adopción, las condiciones del animal
 * y las notas de seguimiento registradas por el administrador.
 * Mapea a la tabla {@code Adoption_Detail}.
 * </p>
 */
@Entity
@Table(name = "Adoption_Detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionDetail {

    /** Identificador único autoincremental del registro. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación 1:1 obligatoria con el vínculo usuario-mascota al que pertenece este detalle.
     * Cada adopción solo puede tener un registro de detalle asociado.
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "user_pet_relationship_id")
    private UserPetRelationship relationship;

    /** Fecha en la que se formalizó la adopción. */
    @Column(name = "adoption_date", nullable = false)
    private LocalDate adoptionDate;

    /** Lugar donde se realizó la entrega de la mascota. */
    @Column(nullable = false)
    private String place;

    /** Descripción de las condiciones del animal en el momento de la adopción. */
    @Column(columnDefinition = "TEXT")
    private String conditions;

    /** Incidencias o problemas detectados durante el proceso de adopción. */
    @Column(columnDefinition = "TEXT")
    private String issues;

    /** Notas adicionales de seguimiento registradas por el administrador. */
    @Column(columnDefinition = "TEXT")
    private String notes;
}
