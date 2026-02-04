package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Representa una donación realizada por un usuario en el sistema
@Entity
@Table(name = "Donation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    // Identificador único de la donación
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación que indica que un usuario puede realizar muchas donaciones
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Fecha y hora exacta en la que se registró la donación
    @Column(nullable = false)
    private LocalDateTime date;

    // Define el tipo de donación (ej. "Efectivo", "Transferencia", "Alimento")
    @Column(nullable = false)
    private String type;

    // Valor monetario o cantidad de la donación
    @Column(nullable = false)
    private Double amount;
}
