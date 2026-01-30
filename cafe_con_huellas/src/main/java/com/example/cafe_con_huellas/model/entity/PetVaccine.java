package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


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

    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    @Column(name = "date_administered", nullable = false)
    private LocalDate dateAdministered;

    @Column(name = "next_dose_date")
    private LocalDate nextDoseDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
