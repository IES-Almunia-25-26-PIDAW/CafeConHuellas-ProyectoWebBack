package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un evento organizado por el refugio.
 * <p>
 * Incluye mercadillos, jornadas de adopción, actividades educativas
 * y cualquier otro acto público del refugio.
 * La fecha de creación se asigna automáticamente.
 * Mapea a la tabla {@code Event}.
 * </p>
 */
@Entity
@Table(name = "Event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    /** Identificador único autoincremental del evento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre descriptivo del evento. Máximo 100 caracteres. */
    @Column(nullable = false, length = 100)
    private String name;

    /** Descripción detallada de las actividades o propósito del evento. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Fecha y hora programada para la realización del evento. */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /** Dirección o lugar físico donde se llevará a cabo el evento. */
    @Column(nullable = false)
    private String location;

    /** URL de la imagen promocional o cartel del evento. */
    @Column(name = "image_url")
    private String imageUrl;

    /** Clasificación del evento según su naturaleza. */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    /** Estado actual del evento en su ciclo de vida. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    /** Aforo máximo de asistentes permitido. Puede ser nulo si no hay límite. */
    @Column(name = "max_capacity")
    private Integer maxCapacity;

    /** Fecha y hora en la que se creó el registro en el sistema. */
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * Asigna automáticamente la fecha de creación si no se ha proporcionado
     * antes de persistir el registro.
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
