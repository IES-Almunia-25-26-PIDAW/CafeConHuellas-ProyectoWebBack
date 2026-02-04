package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio central para la gestión de usuarios y sus credenciales
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
