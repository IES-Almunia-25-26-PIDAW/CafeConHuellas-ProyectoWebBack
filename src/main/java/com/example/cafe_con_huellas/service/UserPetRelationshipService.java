package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserPetRelationshipMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import com.example.cafe_con_huellas.model.entity.RelationshipType;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.repository.AdoptionRequestRepository;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
    private final AdoptionRequestRepository adoptionRequestRepository;
    private final EmailService emailService;

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

        UserPetRelationship savedRelationship = relationshipRepository.save(relationship);

        // 4. Si es ADOPCION, vincular la AdoptionRequest APROBADA con esta relación
        if (newType == RelationshipType.ADOPCION) {
            adoptionRequestRepository
                    .findByFormToken_UserIdAndFormToken_PetIdAndStatus(
                            dto.getUserId(), dto.getPetId(), AdoptionRequestStatus.APROBADA)
                    .ifPresent(request -> {
                        request.setRelationship(savedRelationship);
                        adoptionRequestRepository.save(request);
                    });
        }

        return relationshipMapper.toDto(savedRelationship);
    }

    // ---------- MÉTODOS PARA USUARIO AUTENTICADO ----------

    /**
     * Registra un nuevo vínculo entre el usuario autenticado y una mascota.
     * <p>
     * A diferencia de {@link #save(UserPetRelationshipDTO)}, este método está diseñado
     * para ser invocado por un usuario con rol USER desde la web. Aplica las siguientes
     * restricciones adicionales de seguridad y negocio:
     * </p>
     * <ul>
     *   <li>El {@code userId} se obtiene del email autenticado (JWT), nunca del body.</li>
     *   <li>Solo se permiten los tipos de relación: ACOGIDA, PASEO y VOLUNTARIADO.
     *       El tipo ADOPCION está bloqueado porque tiene su propio flujo con formulario.</li>
     *   <li>La relación se crea siempre con {@code active = false},
     *       pendiente de aprobación por el administrador.</li>
     * </ul>
     *
     * @param email email del usuario autenticado extraído del JWT
     * @param dto   datos del vínculo a registrar (el userId del body se ignora)
     * @return {@link UserPetRelationshipDTO} con el registro creado
     * @throws ResourceNotFoundException si el usuario autenticado o la mascota no existen
     * @throws BadRequestException si el tipo de relación es ADOPCION o se viola alguna regla de exclusividad
     */
    @Transactional
    public UserPetRelationshipDTO saveAsUser(String email, UserPetRelationshipDTO dto) {

        // Tipos de relación permitidos para un usuario normal
        Set<RelationshipType> allowedTypes = Set.of(
                RelationshipType.ACOGIDA,
                RelationshipType.PASEO,
                RelationshipType.VOLUNTARIADO
        );

        // Validamos que el tipo de relación esté permitido para usuarios normales
        RelationshipType requestedType;
        try {
            requestedType = RelationshipType.valueOf(dto.getRelationshipType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Tipo de relación no válido: " + dto.getRelationshipType());
        }

        if (!allowedTypes.contains(requestedType)) {
            throw new BadRequestException(
                    "Los usuarios solo pueden solicitar relaciones de tipo ACOGIDA, PASEO o VOLUNTARIADO. " +
                            "El proceso de ADOPCION requiere un formulario específico.");
        }

        // Obtenemos el usuario autenticado desde la BD usando el email del JWT
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        // Verificamos que la mascota existe
        var pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + dto.getPetId()));

        // Evitamos solicitudes duplicadas del mismo usuario para la misma mascota y tipo
        if (relationshipRepository.existsByUserIdAndPetIdAndRelationshipType(
                user.getId(), dto.getPetId(), requestedType)) {
            throw new BadRequestException(
                    "Ya tienes una solicitud de " + requestedType.name() +
                            " registrada para esta mascota.");
        }

        // Verificamos conflictos de vínculos activos (reutilizamos la lógica existente)
        List<UserPetRelationship> activeLinks = relationshipRepository.findByPetId(dto.getPetId())
                .stream()
                .filter(UserPetRelationship::getActive)
                .toList();

        // Si la mascota ya está adoptada, bloqueamos cualquier nuevo vínculo
        if (activeLinks.stream().anyMatch(r -> r.getRelationshipType() == RelationshipType.ADOPCION)) {
            throw new BadRequestException("Esta mascota ya figura como adoptada y no admite nuevos vínculos.");
        }

        // Construimos la entidad forzando active=false y startDate=hoy
        // El userId siempre viene del JWT, nunca del body
        UserPetRelationship relationship = UserPetRelationship.builder()
                .user(user)
                .pet(pet)
                .relationshipType(requestedType)
                .startDate(LocalDate.now())
                .active(false) // siempre pendiente de aprobación del admin
                .build();

        UserPetRelationship saved = relationshipRepository.save(relationship);
        return relationshipMapper.toDto(saved);
    }

    /**
     * Actualiza los datos de un vínculo existente.
     * <p>
     * Si el campo {@code active} cambia respecto al estado anterior, se envía
     * automáticamente un email al usuario notificándole la decisión:
     * <ul>
     *   <li>Si pasa a {@code true} → email de aceptación.</li>
     *   <li>Si pasa a {@code false} → email de rechazo o cierre.</li>
     * </ul>
     * Aplica para todos los tipos de relación (adopción, acogida, paseo, etc.).
     *
     * @param id  identificador del vínculo a actualizar
     * @param dto nuevos datos del vínculo
     * @return {@link UserPetRelationshipDTO} con los datos actualizados
     * @throws ResourceNotFoundException si no existe el vínculo con ese ID
     */
    @Transactional
    public UserPetRelationshipDTO update(Long id, UserPetRelationshipDTO dto) {
        UserPetRelationship existing = relationshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada con ID: " + id));

        // Guardamos el estado anterior de active para detectar si ha cambiado
        boolean wasActive = existing.getActive();
        boolean isNowActive = dto.getActive();

        // Actualizamos los campos permitidos
        existing.setActive(isNowActive);
        existing.setEndDate(dto.getEndDate());
        existing.setStartDate(dto.getStartDate());
        existing.setRelationshipType(RelationshipType.valueOf(dto.getRelationshipType().toUpperCase()));

        relationshipRepository.save(existing);

        // Si el campo active ha cambiado, notificamos al usuario por email
        if (wasActive != isNowActive) {
            String userEmail = existing.getUser().getEmail();
            String userName  = existing.getUser().getFirstName() + " " + existing.getUser().getLastName1();
            String petName   = existing.getPet().getName();
            String type      = existing.getRelationshipType().name();

            if (isNowActive) {
                emailService.notifyRelationshipAccepted(userEmail, userName, petName, type);
            } else {
                emailService.notifyRelationshipRejected(userEmail, userName, petName, type);
            }
        }

        return relationshipMapper.toDto(existing);
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
