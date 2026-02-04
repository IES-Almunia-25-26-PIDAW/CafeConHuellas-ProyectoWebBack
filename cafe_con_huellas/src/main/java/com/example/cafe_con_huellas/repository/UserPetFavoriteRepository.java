package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para gestionar la lista de las mascotas favoritas de los usuarios
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface UserPetFavoriteRepository extends JpaRepository<UserPetFavorite,Long> {
}
