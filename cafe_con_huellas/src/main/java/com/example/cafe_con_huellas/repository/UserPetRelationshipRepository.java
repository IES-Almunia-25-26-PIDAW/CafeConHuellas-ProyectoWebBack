package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Repositorio para controlar los vínculos (adopción, acogida, etc.) entre personas y mascotas
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface UserPetRelationshipRepository extends JpaRepository<UserPetRelationship,Long> {

    // Relaciones asociadas a un usuario
    List<UserPetRelationship> findByUserId(Long userId);

    // Relaciones asociadas a una mascota
    List<UserPetRelationship> findByPetId(Long petId);

    // Relaciones que siguen activas
    List<UserPetRelationship> findByActiveTrue();

}
