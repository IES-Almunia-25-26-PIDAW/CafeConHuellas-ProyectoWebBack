package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.AdoptionRequest;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la gestión de las solicitudes de adopción.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * </p>
 */
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    /**
     * Filtra las solicitudes por su estado actual.
     * Usado por el administrador para revisar pendientes, aprobadas o denegadas.
     *
     * @param status estado por el que filtrar ({@link AdoptionRequestStatus})
     * @return lista de solicitudes con el estado indicado
     */
    List<AdoptionRequest> findByStatus(AdoptionRequestStatus status);

    /**
     * Comprueba si ya existe una solicitud asociada a un token concreto.
     * Evita que un mismo token genere más de una solicitud.
     *
     * @param formTokenId identificador del token de formulario
     * @return {@code true} si ya existe una solicitud para ese token
     */
    boolean existsByFormTokenId(Long formTokenId);

    /**
     * Busca la solicitud asociada a un token de formulario concreto.
     *
     * @param formTokenId identificador del token de formulario
     * @return {@link Optional} con la solicitud si existe
     */
    Optional<AdoptionRequest> findByFormTokenId(Long formTokenId);

    /**
     * Devuelve todas las solicitudes de adopción cuyo formulario pertenece
     * al usuario con el email indicado.
     * <p>
     * Usado por {@code GET /api/adoption-requests/me} para que el usuario autenticado
     * consulte únicamente sus propias solicitudes. El email se extrae del JWT,
     * nunca se acepta como parámetro del cliente.
     * Se usa JPQL porque el usuario está dos niveles de relación hacia abajo
     * ({@code formToken → user → email}).
     * </p>
     *
     * @param email email del usuario autenticado
     * @return lista de solicitudes asociadas al usuario indicado
     */
    @Query("SELECT r FROM AdoptionRequest r WHERE r.formToken.user.email = :email")
    List<AdoptionRequest> findByUserEmail(String email);

    /**
     * Filtra las solicitudes por el email del usuario vinculado al token.
     *
     * @param email email del usuario
     * @return lista de solicitudes de ese usuario
     */
    List<AdoptionRequest> findByFormTokenUserEmail(String email);
}
