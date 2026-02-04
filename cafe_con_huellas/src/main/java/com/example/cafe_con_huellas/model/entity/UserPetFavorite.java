package com.example.cafe_con_huellas.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Representa la lista de "favoritos" o "intereses" de los usuarios por las mascotas
@Entity
@Table(name = "User_Pet_Favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El usuario que marcó la mascota como favorita
    // Un usuario puede tener muchas mascotas en su lista de favoritos
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // La mascota que fue marcada como favorita
    // Una mascota puede ser la favorita de muchos usuarios diferentes
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
