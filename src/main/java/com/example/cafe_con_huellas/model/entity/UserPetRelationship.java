package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad que representa el vínculo formal entre un usuario y una mascota.
 * <p>
 * Registra los distintos tipos de relación que puede tener un usuario
 * con un animal del refugio (adopción, acogida, paseo, voluntariado),
 * incluyendo las fechas de inicio y fin y si el vínculo sigue activo.
 * Mapea a la tabla {@code User_Pet_Relationship}.
 * </p>
 */
@Entity
@Table(name = "User_Pet_Relationship")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetRelationship {

    /** Identificador único autoincremental del vínculo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que participa en el vínculo.
     * Una misma persona puede tener múltiples vínculos registrados.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Mascota que forma parte del vínculo.
     * Una mascota puede haber tenido diferentes relaciones a lo largo del tiempo.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    /** Tipo de vínculo establecido entre el usuario y la mascota. */
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;

    /** Fecha en la que comenzó el vínculo. */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Fecha de finalización del vínculo. Aplica principalmente a acogidas temporales y paseos. */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** Indica si el vínculo sigue vigente en la actualidad. */
    @Column(nullable = false)
    private Boolean active;


}
