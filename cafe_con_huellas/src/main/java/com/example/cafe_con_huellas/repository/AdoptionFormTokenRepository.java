package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la gestión de los tokens únicos de formularios de adopción.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * </p>
 */
@Repository
public interface AdoptionFormTokenRepository extends JpaRepository<AdoptionFormToken, Long> {

    /**
     * Busca un token por su valor UUID, usado al validar el enlace del formulario.
     *
     * @param token valor UUID del token recibido en la URL
     * @return {@link Optional} con el token si existe
     */
    Optional<AdoptionFormToken> findByToken(String token);

    /**
     * Comprueba si ya existe un token activo (no usado) para una combinación
     * usuario-mascota concreta. Evita enviar formularios duplicados.
     *
     * @param userId identificador del usuario
     * @param petId  identificador de la mascota
     * @return {@code true} si ya hay un token activo para esa combinación
     */
    boolean existsByUserIdAndPetIdAndUsedFalse(Long userId, Long petId);
}