package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.AdoptionRequest;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repositorio para gestionar los formularios
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    // Para que el admin filtre por estado (PENDIENTE, APROBADA, DENEGADA)
    List<AdoptionRequest> findByStatus(AdoptionRequestStatus status);

    // Para verificar que un token no tenga ya una solicitud guardada
    boolean existsByFormTokenId(Long formTokenId);

    // Para buscar la solicitud asociada a un token concreto
    Optional<AdoptionRequest> findByFormTokenId(Long formTokenId);
}
