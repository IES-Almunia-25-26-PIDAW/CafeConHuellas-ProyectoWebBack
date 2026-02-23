package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.RelationshipType;
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
                    .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada con ID: " + id));
    }

    // Registra una nueva relación (adopción, acogida, paseo, etc.)
    public UserPetRelationship save(UserPetRelationship relationship) {
        // Buscamos vínculos activos
        List<UserPetRelationship> activeLinks = relationshipRepository
                .findByPetId(relationship.getPet().getId())
                .stream()
                .filter(UserPetRelationship::getActive)
                .toList();

        // Si ya está ADOPTADO, bloqueamos cualquier nuevo registro
        boolean isAdopted = activeLinks.stream()
                .anyMatch(r -> r.getRelationshipType() == RelationshipType.ADOPCION);

        if (isAdopted) {
            throw new BadRequestException("Esta mascota ya está adoptada y no puede tener nuevos vínculos activos.");
        }

        // Regla para ADOPCIÓN o ACOGIDA (solo puede haber una de estas dos a la vez)
        if (relationship.getActive() &&
                (relationship.getRelationshipType() == RelationshipType.ADOPCION ||
                        relationship.getRelationshipType() == RelationshipType.ACOGIDA)) {

            boolean hasConflict = activeLinks.stream()
                    .anyMatch(r -> r.getRelationshipType() == RelationshipType.ADOPCION ||
                            r.getRelationshipType() == RelationshipType.ACOGIDA);

            if (hasConflict) {
                throw new BadRequestException("La mascota ya tiene un proceso de adopción o acogida en curso.");
            }
        }

        // Autocompletar fecha si falta
        if (relationship.getStartDate() == null) {
            relationship.setStartDate(LocalDate.now());
        }

        return relationshipRepository.save(relationship);
    }


    // Elimina una relación (uso administrativo)
    public void deleteById(Long id) {
        if (!relationshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. La relación no existe.");
        }
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
        relationship.setEndDate(endDate != null ? endDate : LocalDate.now());
        relationshipRepository.save(relationship);
    }
}
