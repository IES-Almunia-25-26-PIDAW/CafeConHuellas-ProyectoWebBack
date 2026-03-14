package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una mascota marcada como favorita por un usuario.
 * <p>
 * Implementa la relación N:M entre {@link User} y {@link Pet}
 * para la funcionalidad de lista de interés o favoritos.
 * Mapea a la tabla {@code User_Pet_Favorites}.
 * </p>
 */
@Entity
@Table(name = "User_Pet_Favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetFavorite {

    /** Identificador único autoincremental del registro. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que marcó la mascota como favorita.
     * Un usuario puede tener múltiples mascotas en su lista.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Mascota marcada como favorita.
     * Una mascota puede ser favorita de múltiples usuarios diferentes.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
