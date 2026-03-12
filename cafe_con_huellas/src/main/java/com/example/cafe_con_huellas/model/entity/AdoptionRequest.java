package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Almacena los datos que el usuario rellena en el formulario público de adopción
// Se vincula 1:1 con el token que le dio acceso al formulario
@Entity
@Table(name = "Adoption_Request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación 1:1 con el token, cada token genera como máximo una solicitud
    @OneToOne(optional = false)
    @JoinColumn(name = "adoption_form_token_id")
    private AdoptionFormToken formToken;

    // --- Datos de vivienda ---
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    // Ej: PISO, CASA, ADOSADO
    @Column(name = "housing_type", nullable = false)
    private String housingType;

    @Column(name = "has_garden", nullable = false)
    private Boolean hasGarden;

    // --- Convivencia ---
    @Column(name = "has_other_pets", nullable = false)
    private Boolean hasOtherPets;

    @Column(name = "has_children", nullable = false)
    private Boolean hasChildren;

    // Horas al día que el animal estaría solo en casa
    @Column(name = "hours_alone_per_day", nullable = false)
    private Integer hoursAlonePerDay;

    // --- Experiencia y motivación ---
    @Column(name = "experience_with_pets", nullable = false)
    private Boolean experienceWithPets;

    @Column(name = "reason_for_adoption", columnDefinition = "TEXT", nullable = false)
    private String reasonForAdoption;

    // Acepta que la protectora pueda hacer seguimiento post-adopción
    @Column(name = "agrees_to_follow_up", nullable = false)
    private Boolean agreesToFollowUp;

    // Campo libre para cualquier info adicional
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    // Estado de la solicitud: PENDIENTE, APROBADA, DENEGADA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionRequestStatus status;

    // Fecha y hora en la que el usuario envió el formulario
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    // Asigna automáticamente la fecha de envío y el estado inicial al crear la solicitud
    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.status = AdoptionRequestStatus.PENDIENTE;
    }
}