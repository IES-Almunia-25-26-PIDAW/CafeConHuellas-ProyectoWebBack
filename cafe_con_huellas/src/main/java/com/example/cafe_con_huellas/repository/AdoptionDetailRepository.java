package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para acceder a los detalles legales y técnicos de las adopciones
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface AdoptionDetailRepository extends JpaRepository<AdoptionDetail,Long> {
    // Busca por la relación
    AdoptionDetail findByRelationshipId(Long relationshipId);

    // Evita duplicados
    boolean existsByRelationshipId(Long relationshipId);

}
