package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad que registra la aplicación de una vacuna a una mascota.
 * <p>
 * Vincula una {@link Pet} con un tipo de {@link Vaccine} e incluye
 * la fecha de administración, la próxima dosis programada y notas clínicas.
 * Mapea a la tabla {@code Pet_Vaccine}.
 * </p>
 */
@Entity
@Table(name = "Pet_Vaccine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetVaccine {

    /** Identificador único autoincremental del registro de vacunación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mascota que recibió la vacuna.
     * Muchas vacunas pueden estar aplicadas a una misma mascota. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    /** Tipo de vacuna aplicada.
     * Muchas aplicaciones de una misma vacuna. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    /** Fecha en la que se administró la vacuna. */
    @Column(name = "date_administered", nullable = false)
    private LocalDate dateAdministered;

    /** Fecha sugerida para el refuerzo o la siguiente dosis. Puede ser nula. */
    @Column(name = "next_dose_date")
    private LocalDate nextDoseDate;

    /** Notas clínicas adicionales (lote de la vacuna, reacciones observadas, etc.). */
    @Column(columnDefinition = "TEXT")
    private String notes;
}
