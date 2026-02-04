package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para controlar los vínculos (adopción, acogida, etc.) entre personas y mascotas
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface UserPetRelationshipRepository extends JpaRepository<UserPetRelationship,Long> {
}
