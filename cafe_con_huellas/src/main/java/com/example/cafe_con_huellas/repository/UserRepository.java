package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositorio central para la gestión de usuarios y sus credenciales
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Busca un usuario por su email
    Optional<User> findByEmail(String email);

    // Comprueba si un email ya está registrado
    boolean existsByEmail(String email);

    // Devuelve usuarios según su rol
    List<User> findByRole(Role role);

    // Buscar por nombre o apellido (sirve para el administrador)
    List<User> findByFirstNameContainingIgnoreCaseOrLastName1ContainingIgnoreCase(String firstName, String lastName1);

    // Contar cuántos usuarios hay de un rol (para estadísticas)
    long countByRole(Role role);

}
