package com.example.cafe_con_huellas.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Representa un token único y temporal para el formulario público de adopción
// Permite que el usuario acceda al formulario sin necesidad de estar logueado
@Entity
@Table(name = "Adoption_Form_Token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionFormToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token UUID único que se enviará en el enlace del correo
    @Column(nullable = false, unique = true)
    private String token;

    // Usuario interesado en la adopción
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Mascota sobre la que se solicita la adopción
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // Fecha de creación del token
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Fecha de expiración, por defecto 48 horas después de crearse
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Indica si el formulario ya fue rellenado y el token usado
    @Column(nullable = false)
    private Boolean used;

    // Asigna automáticamente los valores al crear el token
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // El token expira en 48 horas
        this.expiresAt = LocalDateTime.now().plusHours(48);
        this.used = false;
    }
}