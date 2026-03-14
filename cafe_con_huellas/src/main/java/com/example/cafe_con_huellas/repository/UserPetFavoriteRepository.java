package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la gestión de las mascotas favoritas de los usuarios.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * </p>
 */
@Repository
public interface UserPetFavoriteRepository extends JpaRepository<UserPetFavorite,Long> {

    /**
     * Devuelve todas las mascotas marcadas como favoritas por un usuario concreto.
     *
     * @param userId identificador del usuario
     * @return lista de favoritos del usuario indicado
     */
    List<UserPetFavorite> findByUserId(Long userId);

    /**
     * Comprueba si una mascota ya está en la lista de favoritos de un usuario.
     * Usado para evitar duplicados al añadir favoritos.
     *
     * @param userId identificador del usuario
     * @param petId  identificador de la mascota
     * @return {@code true} si la mascota ya es favorita del usuario
     */
    boolean existsByUserIdAndPetId(Long userId, Long petId);

    /**
     * Elimina una mascota concreta de la lista de favoritos de un usuario.
     *
     * @param userId identificador del usuario
     * @param petId  identificador de la mascota a eliminar de favoritos
     */
    void deleteByUserIdAndPetId(Long userId, Long petId);

}
