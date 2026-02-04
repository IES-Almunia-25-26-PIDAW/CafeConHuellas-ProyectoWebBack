package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Representa a los usuarios del sistema (tanto adoptantes como administradores)
@Entity
@Table(name = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    // Primer apellido obligatorio
    @Column(name = "last_name_1", nullable = false)
    private String lastName1;

    // Segundo apellido opcional
    @Column(name = "last_name_2")
    private String lastName2;

    // El email es obligatorio y no se puede repetir en la base de datos
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    // Guarda el rol como texto (ADMIN o USER) en lugar de números
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Foto de perfil del usuario
    @Column(name = "image_url")
    private String imageUrl;


}
