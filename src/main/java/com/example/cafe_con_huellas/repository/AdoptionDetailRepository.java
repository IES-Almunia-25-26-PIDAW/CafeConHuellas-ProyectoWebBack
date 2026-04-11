package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para el acceso a los detalles post-adopción.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * </p>
 */
@Repository
public interface AdoptionDetailRepository extends JpaRepository<AdoptionDetail,Long> {

    /**
     * Busca el detalle de adopción asociado a una relación usuario-mascota concreta.
     *
     * @param relationshipId identificador de la relación usuario-mascota
     * @return {@link AdoptionDetail} asociado, o {@code null} si no existe
     */
    AdoptionDetail findByRelationshipId(Long relationshipId);

    /**
     * Comprueba si ya existe un detalle registrado para una relación concreta.
     * Usado para evitar registros duplicados.
     *
     * @param relationshipId identificador de la relación a comprobar
     * @return {@code true} si ya existe un detalle para esa relación
     */
    boolean existsByRelationshipId(Long relationshipId);

}
