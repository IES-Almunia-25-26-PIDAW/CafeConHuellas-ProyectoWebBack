package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.AdoptionRequestMapper;
import com.example.cafe_con_huellas.model.entity.*;
import com.example.cafe_con_huellas.repository.AdoptionFormTokenRepository;
import com.example.cafe_con_huellas.repository.AdoptionRequestRepository;
import com.example.cafe_con_huellas.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Servicio encargado de la lógica de negocio de las solicitudes de adopción.
 * <p>
 * Gestiona el ciclo de vida de las solicitudes recibidas a través del formulario
 * público, permitiendo consultarlas y cambiar su estado.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AdoptionRequestService {

    private final AdoptionRequestRepository requestRepository;
    private final AdoptionRequestMapper requestMapper;
    private final AdoptionFormTokenRepository tokenRepository;
    private final PetRepository petRepository;

    /**
     * Persiste una nueva solicitud de adopción a partir del token y el formulario cumplimentado.
     *
     * @param token token UUID que identifica al usuario y la mascota
     * @param dto   datos del formulario rellenado por el usuario
     * @return {@link AdoptionRequestDTO} con el registro persistido
     */
    @Transactional
    public AdoptionRequestDTO save(String token, AdoptionRequestDTO dto) {

        AdoptionFormToken formToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado."));

        // Evitar duplicados si el token ya tiene una solicitud guardada
        if (requestRepository.existsByFormTokenId(formToken.getId())) {
            throw new BadRequestException("Ya existe una solicitud para este formulario.");
        }

        AdoptionRequest entity = requestMapper.toEntity(dto);
        entity.setFormToken(formToken);

        return requestMapper.toDto(requestRepository.save(entity));
    }

    /**
     * Obtiene todas las solicitudes de adopción convertidas a DTO.
     *
     * @return lista de {@link AdoptionRequestDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<AdoptionRequestDTO> findAll() {
        return requestRepository.findAll().stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Filtra las solicitudes por su estado actual.
     *
     * @param status estado por el que filtrar ({@link AdoptionRequestStatus})
     * @return lista de {@link AdoptionRequestDTO} con el estado indicado
     */
    @Transactional(readOnly = true)
    public List<AdoptionRequestDTO> findByStatus(AdoptionRequestStatus status) {
        return requestRepository.findByStatus(status).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las solicitudes de adopción del usuario autenticado identificado por su email.
     * <p>
     * El email se extrae del JWT en el controlador mediante {@code SecurityContextHolder},
     * nunca se acepta como parámetro del cliente.
     * </p>
     *
     * @param email email del usuario autenticado (subject del JWT)
     * @return lista de {@link AdoptionRequestDTO} del usuario indicado
     */
    @Transactional(readOnly = true)
    public List<AdoptionRequestDTO> findByUserEmail(String email) {
        return requestRepository.findByUserEmail(email).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca una solicitud concreta por su identificador.
     *
     * @param id identificador único de la solicitud
     * @return {@link AdoptionRequestDTO} con los datos de la solicitud
     * @throws ResourceNotFoundException si no existe la solicitud con ese ID
     */
    @Transactional(readOnly = true)
    public AdoptionRequestDTO findById(Long id) {
        return requestRepository.findById(id)
                .map(requestMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));
    }

    /**
     * Actualiza el estado de una solicitud de adopción.
     * <p>
     * Si el nuevo estado es {@link AdoptionRequestStatus#APROBADA}, actualiza
     * automáticamente el {@code adoptionStatus} de la mascota asociada a
     * {@link AdoptionStatus#ADOPTADO} dentro de la misma transacción,
     * garantizando consistencia entre ambas entidades.
     * </p>
     *
     * @param id     identificador de la solicitud a actualizar
     * @param status nuevo estado de la solicitud
     * @return {@link AdoptionRequestDTO} con los datos actualizados
     * @throws ResourceNotFoundException si no existe la solicitud con ese ID
     */
    @Transactional
    public AdoptionRequestDTO updateStatus(Long id, AdoptionRequestStatus status) {
        AdoptionRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));

        request.setStatus(status);

        // Si se aprueba la solicitud, la mascota pasa automáticamente a ADOPTADO
        if (status == AdoptionRequestStatus.APROBADA) {
            Pet pet = request.getFormToken().getPet();
            pet.setAdoptionStatus(AdoptionStatus.ADOPTADO);
            petRepository.save(pet);
        }

        return requestMapper.toDto(requestRepository.save(request));
    }

    /**
     * Busca la solicitud de adopción vinculada a una relación usuario-mascota concreta.
     * Usado por el administrador para consultar el formulario original
     * a partir del ID de la relación que generó la adopción.
     *
     * @param relationshipId identificador de la relación usuario-mascota
     * @return {@link AdoptionRequestDTO} con los datos de la solicitud
     * @throws ResourceNotFoundException si no existe solicitud vinculada a esa relación
     */
    @Transactional(readOnly = true)
    public AdoptionRequestDTO findByRelationshipId(Long relationshipId) {
        return requestRepository.findByRelationshipId(relationshipId)
                .map(requestMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró solicitud de adopción para la relación con ID: " + relationshipId));
    }
}