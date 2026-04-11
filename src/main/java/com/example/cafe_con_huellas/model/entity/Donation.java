package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una donación registrada en el sistema.
 * <p>
 * Soporta tanto donaciones de usuarios registrados como donaciones anónimas
 * (con {@code user} a {@code null}). La fecha se asigna automáticamente
 * si no se proporciona.
 * Mapea a la tabla {@code Donation}.
 * </p>
 */
@Entity
@Table(name = "Donation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    /** Identificador único autoincremental de la donación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * Usuario que realizó la donación.
     * Puede ser {@code null} si la donación es anónima.
     * Relación que indica que un usuario puede realizar muchas donaciones
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    /** Fecha y hora exacta en la que se registró la donación. */
    @Column(nullable = false, insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime date;

    /** Categoría de la donación (ej: MONETARIA, ALIMENTACION, MATERIAL). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationCategory category;

    /** Método utilizado para realizar la donación (ej: BIZUM, TRANSFERENCIA). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationMethod method;

    /** Importe o cantidad de la donación. */
    @Column(nullable = false)
    private BigDecimal amount;

    /** Notas adicionales sobre la donación. */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Asigna automáticamente la fecha actual si no se ha proporcionado
     * antes de persistir el registro.
     */
    @PrePersist
    protected void onCreate() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }


}
