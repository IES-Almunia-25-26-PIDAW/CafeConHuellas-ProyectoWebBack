package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Registra el historial de vacunas aplicadas a cada mascota
@Entity
@Table(name = "Pet_Vaccine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetVaccine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mascota que recibió la vacuna (Muchas vacunas aplicadas a una mascota)
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // Referencia al tipo de vacuna aplicada (Muchas aplicaciones de una misma vacuna)
    @ManyToOne(optional = false)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    // Fecha exacta en la que se puso la vacuna
    @Column(name = "date_administered", nullable = false)
    private LocalDate dateAdministered;

    // Fecha sugerida para el refuerzo o siguiente aplicación (puede ser nulo)
    @Column(name = "next_dose_date")
    private LocalDate nextDoseDate;

    // Detalles adicionales, como lote de la vacuna o reacciones observadas
    @Column(columnDefinition = "TEXT")
    private String notes;
}
