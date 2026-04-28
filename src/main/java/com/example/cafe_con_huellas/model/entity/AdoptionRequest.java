package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que almacena los datos del formulario de adopción cumplimentado por el usuario.
 * <p>
 * Recoge información sobre la vivienda, convivencia, experiencia con animales
 * y motivación del solicitante. Se vincula 1:1 con el {@link AdoptionFormToken}
 * que le dio acceso al formulario, y opcionalmente con la {@link UserPetRelationship}
 * generada cuando el administrador aprueba la solicitud.
 * Mapea a la tabla {@code Adoption_Request}.
 * </p>
 */
@Entity
@Table(name = "Adoption_Request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionRequest {

    /** Identificador único autoincremental de la solicitud. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Token que identificó al usuario al acceder al formulario.
     * Cada token genera como máximo una solicitud.
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "adoption_form_token_id")
    private AdoptionFormToken formToken;

    // --- Datos de vivienda ---

    /** Dirección postal del solicitante. */
    @Column(nullable = false)
    private String address;

    /** Ciudad de residencia del solicitante. */
    @Column(nullable = false)
    private String city;

    /** Tipo de vivienda del solicitante (ej: PISO, CASA, ADOSADO). */
    @Column(name = "housing_type", nullable = false)
    private String housingType;

    /** Indica si la vivienda dispone de jardín o zona exterior. */
    @Column(name = "has_garden", nullable = false)
    private Boolean hasGarden;

    // --- Convivencia ---

    /** Indica si el solicitante convive con otras mascotas. */
    @Column(name = "has_other_pets", nullable = false)
    private Boolean hasOtherPets;

    /** Indica si hay menores de edad en el hogar. */
    @Column(name = "has_children", nullable = false)
    private Boolean hasChildren;

    /** Horas al día que el animal estaría solo en casa. */
    @Column(name = "hours_alone_per_day", nullable = false)
    private Integer hoursAlonePerDay;

    // --- Experiencia y motivación ---

    /** Indica si el solicitante tiene experiencia previa con animales. */
    @Column(name = "experience_with_pets", nullable = false)
    private Boolean experienceWithPets;

    /** Motivo principal por el que el solicitante desea adoptar. */
    @Column(name = "reason_for_adoption", columnDefinition = "TEXT", nullable = false)
    private String reasonForAdoption;

    /** Indica si el solicitante acepta que la protectora realice seguimiento post-adopción. */
    @Column(name = "agrees_to_follow_up", nullable = false)
    private Boolean agreesToFollowUp;

    /** Campo libre para información adicional que el solicitante quiera aportar. */
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    /**
     * Relación usuario-mascota que se generó al aprobar esta solicitud.
     * Es nullable porque se vincula después de la aprobación,
     * no en el momento de enviar el formulario.
     */
    @OneToOne
    @JoinColumn(name = "user_pet_relationship_id")
    private UserPetRelationship relationship;

    /**
     * Estado actual de la solicitud.
     *
     * @see AdoptionRequestStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionRequestStatus status;

    /** Fecha y hora en la que el usuario envió el formulario. */
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    /**
     * Asigna automáticamente la fecha de envío y establece el estado inicial
     * como {@link AdoptionRequestStatus#PENDIENTE} antes de persistir.
     */
    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.status = AdoptionRequestStatus.PENDIENTE;
    }
}