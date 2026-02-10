package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Servicio encargado de la gestión de relaciones entre usuarios y mascotas
@Service
@RequiredArgsConstructor
public class UserPetRelationshipService {

    private final UserPetRelationshipRepository relationshipRepository;

    // ---------- CRUD BÁSICO ----------

    // Devuelve todas las relaciones registradas
    public List<UserPetRelationship> findAll() {
        return relationshipRepository.findAll();
    }

    // Busca una relación por su ID
    public UserPetRelationship findById(Long id) {
        return relationshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relationship not found"));
    }

    // Registra una nueva relación (adopción, acogida, paseo, etc.)
    public UserPetRelationship save(UserPetRelationship relationship) {
        return relationshipRepository.save(relationship);
    }

    // Elimina una relación (uso administrativo)
    public void deleteById(Long id) {
        relationshipRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Devuelve todas las relaciones de un usuario
    public List<UserPetRelationship> findByUserId(Long userId) {
        return relationshipRepository.findByUserId(userId);
    }

    // Devuelve todas las relaciones de una mascota
    public List<UserPetRelationship> findByPetId(Long petId) {
        return relationshipRepository.findByPetId(petId);
    }

    // Devuelve solo las relaciones que siguen activas
    public List<UserPetRelationship> findActiveRelationships() {
        return relationshipRepository.findByActiveTrue();
    }

    // Finaliza una relación (por ejemplo, fin de acogida o paseo)
    public void endRelationship(Long relationshipId, LocalDate endDate) {
        UserPetRelationship relationship = findById(relationshipId);
        relationship.setActive(false);
        relationship.setEndDate(endDate);
        relationshipRepository.save(relationship);
    }
}
