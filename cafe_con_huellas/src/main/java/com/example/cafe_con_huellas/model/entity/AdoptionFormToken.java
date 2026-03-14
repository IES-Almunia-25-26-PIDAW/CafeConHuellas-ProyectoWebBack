package com.example.cafe_con_huellas.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un token único y temporal para el formulario público de adopción.
 * <p>
 * Permite al usuario acceder al formulario sin necesidad de estar autenticado.
 * El token se genera como UUID, tiene una validez de 48 horas y solo puede usarse una vez.
 * Mapea a la tabla {@code Adoption_Form_Token}.
 * </p>
 */
@Entity
@Table(name = "Adoption_Form_Token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionFormToken {

    /** Identificador único autoincremental del token. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token UUID único que se envía en el enlace del correo electrónico. */
    @Column(nullable = false, unique = true)
    private String token;

    /** Usuario interesado en la adopción al que se envía el formulario. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** Mascota sobre la que se solicita la adopción. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    /** Fecha y hora en la que se generó el token. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Fecha y hora de expiración del token (48 horas tras su creación). */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Indica si el formulario ya fue rellenado y el token ha sido consumido. */
    @Column(nullable = false)
    private Boolean used;


    /**
     * Solicitud de adopción generada cuando el usuario completa el formulario.
     * Relación 1:1 inversa gestionada por {@link AdoptionRequest}.
     */
    @OneToOne(mappedBy = "formToken")
    private AdoptionRequest adoptionRequest;

    /**
     * Asigna automáticamente la fecha de creación, la fecha de expiración
     * y marca el token como no utilizado antes de persistirlo.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // El token expira en 48 horas
        this.expiresAt = LocalDateTime.now().plusHours(48);
        this.used = false;
    }
}