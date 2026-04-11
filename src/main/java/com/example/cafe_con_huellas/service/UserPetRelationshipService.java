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

/**
 * Servicio encargado de la lógica de negocio de los vínculos entre usuarios y mascotas.
 * <p>
 * Aplica reglas estrictas de exclusividad: una mascota adoptada no puede tener
 * nuevos vínculos, y solo puede haber un proceso de adopción o acogida activo a la vez.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserPetRelationshipService {

    private final UserPetRelationshipRepository relationshipRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserPetRelationshipMapper relationshipMapper;

    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todos los vínculos registrados en el sistema convertidos a DTO.
     *
     * @return lista de {@link UserPetRelationshipDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findAll() {
        return relationshipRepository.findAll().stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un vínculo por su identificador.
     *
     * @param id identificador único del vínculo
     * @return {@link UserPetRelationshipDTO} con los datos del registro
     * @throws ResourceNotFoundException si no existe el vínculo con ese ID
     */
    @Transactional(readOnly = true)
    public UserPetRelationshipDTO findById(Long id) {
        return relationshipRepository.findById(id)
                .map(relationshipMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada con ID: " + id));
    }

    /**
     * Registra un nuevo vínculo entre un usuario y una mascota.
     * <p>
     * Aplica las siguientes reglas de negocio antes de persistir:
     * una mascota adoptada no admite nuevos vínculos, y no puede haber
     * más de un proceso de adopción o acogida activo simultáneamente.
     * </p>
     *
     * @param dto datos del vínculo a registrar
     * @return {@link UserPetRelationshipDTO} con el registro creado
     * @throws ResourceNotFoundException si el usuario o la mascota no existen
     * @throws BadRequestException si se viola alguna regla de exclusividad
     */
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

    /**
     * Elimina un vínculo del sistema por su identificador.
     *
     * @param id identificador del vínculo a eliminar
     * @throws ResourceNotFoundException si no existe el vínculo con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!relationshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: El registro no existe.");
        }
        relationshipRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    /**
     * Obtiene el historial de vínculos de un usuario específico.
     *
     * @param userId identificador del usuario
     * @return lista de {@link UserPetRelationshipDTO} del usuario indicado
     */
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findByUserId(Long userId) {
        return relationshipRepository.findByUserId(userId).stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la trazabilidad completa de vínculos que ha tenido una mascota específica.
     * <p>
     * Devuelve tanto los procesos activos como los históricos,
     * permitiendo conocer quién ha cuidado o adoptado al animal.
     * </p>
     *
     * @param petId identificador de la mascota
     * @return lista de {@link UserPetRelationshipDTO} asociados a la mascota
     */
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findByPetId(Long petId) {
        return relationshipRepository.findByPetId(petId).stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los vínculos activos actualmente en el refugio.
     *
     * @return lista de {@link UserPetRelationshipDTO} con los procesos en curso
     */
    @Transactional(readOnly = true)
    public List<UserPetRelationshipDTO> findActiveRelationships() {
        return relationshipRepository.findByActiveTrue().stream()
                .map(relationshipMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Cierra un vínculo activo asignando la fecha de fin.
     * <p>
     * Si no se proporciona fecha de cierre, se usa la fecha actual.
     * </p>
     *
     * @param relationshipId identificador del vínculo a cerrar
     * @param endDate        fecha de cierre, o {@code null} para usar la fecha actual
     * @throws ResourceNotFoundException si no existe el vínculo con ese ID
     */
    @Transactional
    public void endRelationship(Long relationshipId, LocalDate endDate) {
        UserPetRelationship relationship = relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede finalizar. Relación no encontrada"));

        relationship.setActive(false);
        relationship.setEndDate(endDate != null ? endDate : LocalDate.now());
        relationshipRepository.save(relationship);
    }
}
