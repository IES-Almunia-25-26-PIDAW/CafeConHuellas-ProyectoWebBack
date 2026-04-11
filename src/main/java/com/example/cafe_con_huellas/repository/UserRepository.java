package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA central para la gestión de usuarios y sus credenciales.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * El email se usa como identificador único de autenticación.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de email.
     * Usado por Spring Security durante el proceso de autenticación.
     *
     * @param email email del usuario a buscar
     * @return {@link Optional} con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Comprueba si un email ya está registrado en el sistema.
     * Usado para validar unicidad antes de registrar un nuevo usuario.
     *
     * @param email email a comprobar
     * @return {@code true} si el email ya existe en la base de datos
     */
    boolean existsByEmail(String email);

    /**
     * Devuelve todos los usuarios que tienen un rol específico.
     *
     * @param role rol por el que filtrar ({@link Role})
     * @return lista de usuarios con el rol indicado
     */
    List<User> findByRole(Role role);

    /**
     * Busca usuarios cuyo nombre o primer apellido contengan el texto indicado,
     * ignorando mayúsculas. Útil para el buscador del panel de administración.
     *
     * @param firstName texto a buscar en el nombre
     * @param lastName1 texto a buscar en el primer apellido
     * @return lista de usuarios que coinciden con alguno de los criterios
     */
    List<User> findByFirstNameContainingIgnoreCaseOrLastName1ContainingIgnoreCase(String firstName, String lastName1);

    /**
     * Cuenta el número de usuarios registrados con un rol específico.
     * Útil para estadísticas del panel de administración.
     *
     * @param role rol por el que contar ({@link Role})
     * @return número de usuarios con el rol indicado
     */
    long countByRole(Role role);

}
