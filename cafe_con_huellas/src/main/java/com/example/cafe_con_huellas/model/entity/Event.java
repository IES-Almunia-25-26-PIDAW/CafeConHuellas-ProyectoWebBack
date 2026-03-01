package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Representa un evento organizado por el refugio (mercadillos, jornadas de adopción, etc.)
@Entity
@Table(name = "Event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    // Identificador único del evento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre descriptivo del evento
    @Column(nullable = false, length = 100)
    private String name;

    // Descripción detallada de las actividades o propósito del evento
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Fecha y hora programada para la realización del evento
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    // Dirección o lugar físico donde se llevará a cabo
    @Column(nullable = false)
    private String location;

    // URL de la imagen promocional o cartel del evento
    @Column(name = "image_url")
    private String imageUrl;

    // Clasificación del evento (ej. ADOPCION, MERCADILLO, EDUCACION)
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    // Estado actual del evento (ej. PROGRAMADO, EN_CURSO, FINALIZADO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    // Aforo máximo de personas permitido (opcional)
    @Column(name = "max_capacity")
    private Integer maxCapacity;

    // Fecha y hora en la que se creó el registro en el sistema
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // Método que asegura que la fecha de creación se asigne si no viene de la BD
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
