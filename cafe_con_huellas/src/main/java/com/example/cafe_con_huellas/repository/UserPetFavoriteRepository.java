package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio para gestionar la lista de las mascotas favoritas de los usuarios
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface UserPetFavoriteRepository extends JpaRepository<UserPetFavorite,Long> {

    // Devuelve todas las mascotas favoritas de un usuario
    List<UserPetFavorite> findByUserId(Long userId);

    // Comprueba si una mascota ya está en favoritos para un usuario
    boolean existsByUserIdAndPetId(Long userId, Long petId);

    // Elimina una mascota concreta de los favoritos de un usuario
    void deleteByUserIdAndPetId(Long userId, Long petId);

}
