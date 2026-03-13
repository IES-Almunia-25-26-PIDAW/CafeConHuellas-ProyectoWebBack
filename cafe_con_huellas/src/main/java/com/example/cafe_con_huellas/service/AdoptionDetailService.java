package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.AdoptionDetailMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import com.example.cafe_con_huellas.repository.AdoptionDetailRepository;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Servicio encargado de la lógica de negocio relacionada con los detalles post-adopción.
 * <p>
 * Gestiona el registro y seguimiento de la información técnica asociada
 * a una adopción completada, validando que no existan duplicados por relación.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AdoptionDetailService {

    private final AdoptionDetailRepository adoptionDetailRepository;
    private final AdoptionDetailMapper adoptionDetailMapper;
    private final UserPetRelationshipRepository relationshipRepository;
    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todos los registros de detalles de adopción convertidos a DTO.
     *
     * @return lista de {@link AdoptionDetailDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<AdoptionDetailDTO> findAll() {
        return adoptionDetailRepository.findAll().stream()
                .map(adoptionDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un detalle de adopción por su identificador.
     *
     * @param id identificador único del detalle
     * @return {@link AdoptionDetailDTO} con los datos del registro
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional(readOnly = true)
    public AdoptionDetailDTO findById(Long id) {
        return adoptionDetailRepository.findById(id)
                .map(adoptionDetailMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de adopción no encontrado con ID: " + id));
    }

    /**
     * Registra un nuevo detalle de adopción.
     * <p>
     * Valida que no exista ya un detalle registrado para la misma relación
     * usuario-mascota antes de persistir.
     * </p>
     *
     * @param dto datos del detalle a registrar
     * @return {@link AdoptionDetailDTO} con el registro persistido
     * @throws BadRequestException si ya existen detalles para esa relación
     * @throws ResourceNotFoundException si la relación referenciada no existe
     */
    @Transactional
    public AdoptionDetailDTO save(AdoptionDetailDTO dto) {
        // Validación de duplicados. Usamos el método del repositorio para evitar duplicados en la misma relación
        if (dto.getId() == null && adoptionDetailRepository.existsByRelationshipId(dto.getUserPetRelationshipId())) {
            throw new BadRequestException("Ya existen detalles registrados para esta relación.");
        }

        // Buscar el objeto padre real
        var relationship = relationshipRepository.findById(dto.getUserPetRelationshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada con ID: " + dto.getUserPetRelationshipId()));

        // Convertir dto a entidad
        AdoptionDetail entity = adoptionDetailMapper.toEntity(dto);

        // Asignar objeto completo
        entity.setRelationship(relationship);

        AdoptionDetail savedEntity = adoptionDetailRepository.save(entity);
           return adoptionDetailMapper.toDto(savedEntity);
    }

    /**
     * Elimina un registro de detalles de adopción por su identificador.
     *
     * @param id identificador del registro a eliminar
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!adoptionDetailRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Registro no encontrado.");
        }
        adoptionDetailRepository.deleteById(id);
    }

        // ---------- CONSULTAS ESPECÍFICAS ----------

    /**
     * Busca los detalles de adopción asociados a una relación usuario-mascota específica.
     *
     * @param relationshipId identificador de la relación
     * @return {@link AdoptionDetailDTO} con los detalles del seguimiento
     * @throws ResourceNotFoundException si no existen detalles para esa relación
     */
    @Transactional(readOnly = true)
    public AdoptionDetailDTO findByRelationshipId(Long relationshipId) {
        AdoptionDetail detail = adoptionDetailRepository.findByRelationshipId(relationshipId);
        if (detail == null) {
            throw new ResourceNotFoundException("No se encontraron detalles para la relación con ID: " + relationshipId);
        }
        return adoptionDetailMapper.toDto(detail);
    }

        // ---------- ACTUALIZACIÓN CONTROLADA ----------

    /**
     * Actualiza los campos de seguimiento de un detalle de adopción existente.
     * <p>
     * Solo modifica los campos editables: lugar, condiciones, incidencias y notas.
     * No altera la relación usuario-mascota asociada.
     * </p>
     *
     * @param id  identificador del detalle a actualizar
     * @param dto nuevos datos de seguimiento
     * @return {@link AdoptionDetailDTO} con los datos actualizados
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional
    public AdoptionDetailDTO updateDetails(Long id, AdoptionDetailDTO dto) {
        // Verificamos existencia antes de proceder
        AdoptionDetail detail = adoptionDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar. Detalle no encontrado"));

        // Sincronizamos los cambios manuales (Place, Conditions, Issues, Notes)
        detail.setPlace(dto.getPlace());
        detail.setConditions(dto.getConditions());
        detail.setIssues(dto.getIssues());
        detail.setNotes(dto.getNotes());

        return adoptionDetailMapper.toDto(adoptionDetailRepository.save(detail));
    }


}
