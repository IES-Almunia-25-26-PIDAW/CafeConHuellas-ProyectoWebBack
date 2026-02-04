package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Define el vínculo formal entre un usuario y una mascota (ej. adopción, acogida)
@Entity
@Table(name = "User_Pet_Relationship")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El usuario (humano) que participa en la relación
    // Permite que una misma persona pueda tener múltiples vínculos registrados
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // La mascota que forma parte del vínculo
    // Permite que una mascota haya tenido diferentes relaciones a lo largo del tiempo
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // Clasifica si la relación es adopción, acogida, etc.
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // Fecha de finalización ( aplica para acogidas temporales o paseos)
    @Column(name = "end_date")
    private LocalDate endDate;

    // Indica si el vínculo sigue vigente (ej. si la mascota sigue en acogida)
    @Column(nullable = false)
    private Boolean active;


}
