package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "Adoption_Detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_pet_relationship_id")
    private UserPetRelationship relationship;

    @Column(name = "adoption_date", nullable = false)
    private LocalDate adoptionDate;

    @Column(nullable = false)
    private String place;

    @Column(columnDefinition = "TEXT")
    private String conditions;

    @Column(columnDefinition = "TEXT")
    private String issues;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
