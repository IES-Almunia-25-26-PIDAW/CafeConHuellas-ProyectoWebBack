package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa a un usuario del sistema.
 * <p>
 * Abarca tanto a los adoptantes registrados como a los administradores.
 * El email es único y se usa como identificador de autenticación.
 * La contraseña se almacena encriptada con BCrypt.
 * Mapea a la tabla {@code User}.
 * </p>
 */
@Entity
@Table(name = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Identificador único autoincremental del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de pila del usuario. */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** Primer apellido del usuario (Obligatorio). */
    @Column(name = "last_name_1", nullable = false)
    private String lastName1;

    /** Segundo apellido del usuario. Opcional. */
    @Column(name = "last_name_2")
    private String lastName2;

    /** Email del usuario. Único en el sistema y usado como identificador de login. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Contraseña del usuario almacenada como hash BCrypt. Nunca en texto plano. */
    @Column(nullable = false)
    private String password;

    /** Número de teléfono de contacto del usuario. Opcional. */
    private String phone;

    /** Rol del usuario en el sistema (ADMIN o USER). Almacenado como texto. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** URL de la foto de perfil del usuario. Opcional. */
    @Column(name = "image_url")
    private String imageUrl;


}
