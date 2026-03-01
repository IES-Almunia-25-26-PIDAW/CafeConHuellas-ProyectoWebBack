package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserPetRelationshipMapper;
import com.example.cafe_con_huellas.model.entity.RelationshipType;
import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Servicio encargado de la gestión de relaciones entre usuarios y mascotas
@Service
@RequiredArgsConstructor
public class UserPetRelationshipService {

    private final UserPetRelationshipRepository relationshipRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserPetRelationshipMapper relationshipMapper;

    // ---------- CRUD BÁSICO ----------

    // Obtiene el listado de todas las relaciones en formato DTO
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findAll() {
        return relationshipRepository.findAll().stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca un vínculo específico por su ID y lo devuelve como DTO
    @Transactional(readOnly = true)
    public UserPetRelationshipDTO findById(Long id) {
        return relationshipRepository.findById(id)
                .map(relationshipMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada con ID: " + id));
    }

    // Registra un nuevo vínculo aplicando reglas estrictas de exclusividad para adopciones y acogidas
    @Transactional
    public UserPetRelationshipDTO save(UserPetRelationshipDTO dto) {
        // 1. Validaciones de existencia de Entidades
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUserId()));
        var pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + dto.getPetId()));

        // 2. Lógica de Negocio: Verificar conflictos de vínculos activos
        List<UserPetRelationship> activeLinks = relationshipRepository.findByPetId(dto.getPetId())
                .stream()
                .filter(UserPetRelationship::getActive)
                .toList();

        // Si ya está ADOPTADO, bloqueamos cualquier intento de nuevo registro
        if (activeLinks.stream().anyMatch(r -> r.getRelationshipType() == RelationshipType.ADOPCION)) {
            throw new BadRequestException("Esta mascota ya figura como adoptada y no admite nuevos vínculos.");
        }

        // Si es ADOPCIÓN o ACOGIDA, solo puede haber uno de estos procesos a la vez
        RelationshipType newType = RelationshipType.valueOf(dto.getRelationshipType().toUpperCase());
        if (dto.getActive() && (newType == RelationshipType.ADOPCION || newType == RelationshipType.ACOGIDA)) {
            boolean hasConflict = activeLinks.stream()
                    .anyMatch(r -> r.getRelationshipType() == RelationshipType.ADOPCION ||
                            r.getRelationshipType() == RelationshipType.ACOGIDA);
            if (hasConflict) {
                throw new BadRequestException("La mascota ya tiene un proceso de adopción o acogida en curso.");
            }
        }

        // 3. Mapeo y Persistencia
        UserPetRelationship relationship = relationshipMapper.toEntity(dto);
        relationship.setUser(user);
        relationship.setPet(pet);

        if (relationship.getStartDate() == null) {
            relationship.setStartDate(LocalDate.now());
        }

        return relationshipMapper.toDto(relationshipRepository.save(relationship));
    }

    // Elimina un registro de relación (Acción restringida a administradores)
    @Transactional
    public void deleteById(Long id) {
        if (!relationshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: El registro no existe.");
        }
        relationshipRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Devuelve el historial de vínculos de un usuario concreto
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findByUserId(Long userId) {
        return relationshipRepository.findByUserId(userId).stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    // Devuelve el historial de quién ha cuidado o adoptado a una mascota específica
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findByPetId(Long petId) {
        return relationshipRepository.findByPetId(petId).stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    // Devuelve todos los procesos que se encuentran "en curso" actualmente
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findActiveRelationships() {
        return relationshipRepository.findByActiveTrue().stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    // Cierra un vínculo activo (por ejemplo, al finalizar un periodo de acogida o paseo)
    @Transactional
    public void endRelationship(Long relationshipId, LocalDate endDate) {
        UserPetRelationship relationship = relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede finalizar. Relación no encontrada"));

        relationship.setActive(false);
        relationship.setEndDate(endDate != null ? endDate : LocalDate.now());
        relationshipRepository.save(relationship);
    }
}
