package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la gestión de los vínculos entre usuarios y mascotas.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * Proporciona filtros por usuario, mascota y estado activo del vínculo.
 * </p>
 */
@Repository
public interface UserPetRelationshipRepository extends JpaRepository<UserPetRelationship,Long> {

    /**
     * Devuelve todos los vínculos asociados a un usuario concreto.
     *
     * @param userId identificador del usuario
     * @return lista de vínculos del usuario indicado
     */
    List<UserPetRelationship> findByUserId(Long userId);

    /**
     * Devuelve todos los vínculos asociados a una mascota concreta.
     *
     * @param petId identificador de la mascota
     * @return lista de vínculos de la mascota indicada
     */
    List<UserPetRelationship> findByPetId(Long petId);

    /**
     * Devuelve únicamente los vínculos que están actualmente activos.
     *
     * @return lista de vínculos con {@code active} igual a {@code true}
     */
    List<UserPetRelationship> findByActiveTrue();

    /**
     * Devuelve los vínculos que siguen marcados como activos pero cuya fecha de fin
     * ya ha pasado. Usado por el scheduler diario para desactivarlos automáticamente.
     *
     * @param date fecha de referencia (normalmente {@code LocalDate.now()})
     * @return lista de vínculos activos con {@code endDate} anterior a la fecha indicada
     */
    List<UserPetRelationship> findByActiveTrueAndEndDateBefore(LocalDate date);

}
