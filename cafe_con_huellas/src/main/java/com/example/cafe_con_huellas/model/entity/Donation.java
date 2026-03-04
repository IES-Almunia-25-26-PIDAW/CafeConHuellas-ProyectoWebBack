package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // Fecha y hora exacta en la que se registró la donación
    @Column(nullable = false, insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime date;

    // Define el tipo de donación (ej. "Monetaria", "Material", "Alimento")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationCategory category;

    // Define el método de donación (ej. "Transferencia", "Bizum", "Efectivo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationMethod method;

    // Valor monetario o cantidad de la donación
    @Column(nullable = false)
    private BigDecimal amount;

    // Notas adicionales
    @Column(columnDefinition = "TEXT")
    private String notes;

    // Método que asegura que la fecha se asigne si no viene de la BD
    @PrePersist
    protected void onCreate() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }


}
