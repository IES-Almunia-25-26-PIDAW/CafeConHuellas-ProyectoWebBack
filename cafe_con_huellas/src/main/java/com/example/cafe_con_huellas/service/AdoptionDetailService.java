package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.AdoptionDetailMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import com.example.cafe_con_huellas.repository.AdoptionDetailRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdoptionDetailService {

        private final AdoptionDetailRepository adoptionDetailRepository;
        private final AdoptionDetailMapper adoptionDetailMapper;

        // ---------- CRUD BÁSICO ----------

        // Obtiene todos los registros de detalles de adopción convertidos a DTO
        @Transactional(readOnly = true)
        public List<AdoptionDetailDTO> findAll() {
            return adoptionDetailRepository.findAll().stream()
                    .map(adoptionDetailMapper::toDto)
                    .collect(Collectors.toList());
        }

        // Busca un detalle específico por su ID y lo devuelve como DTO
        @Transactional(readOnly = true)
        public AdoptionDetailDTO findById(Long id) {
            return adoptionDetailRepository.findById(id)
                    .map(adoptionDetailMapper::toDto)
                    .orElseThrow(() -> new ResourceNotFoundException("Detalle de adopción no encontrado con ID: " + id));
        }

        // Registra un nuevo detalle de adopción validando que no exista duplicidad en la relación
        @Transactional
        public AdoptionDetailDTO save(AdoptionDetailDTO dto) {
            // Validación: Usamos el método del repositorio para evitar duplicados en la misma relación
            if (dto.getId() == null && adoptionDetailRepository.existsByRelationshipId(dto.getUserPetRelationshipId())) {
                throw new BadRequestException("Ya existen detalles registrados para esta relación de adopción.");
            }

            // Convertimos el DTO a Entidad, guardamos y devolvemos el DTO resultante
            AdoptionDetail entity = adoptionDetailMapper.toEntity(dto);
            AdoptionDetail savedEntity = adoptionDetailRepository.save(entity);

            return adoptionDetailMapper.toDto(savedEntity);
        }

        // Elimina un registro de detalles por su identificador único
        @Transactional
        public void deleteById(Long id) {
            if (!adoptionDetailRepository.existsById(id)) {
                throw new ResourceNotFoundException("No se puede eliminar. Registro no encontrado.");
            }
            adoptionDetailRepository.deleteById(id);
        }

        // ---------- CONSULTAS ESPECÍFICAS ----------

        // Busca detalles asociados a un ID de relación específico
        @Transactional(readOnly = true)
        public AdoptionDetailDTO findByRelationshipId(Long relationshipId) {
            AdoptionDetail detail = adoptionDetailRepository.findByRelationshipId(relationshipId);
            if (detail == null) {
                throw new ResourceNotFoundException("No se encontraron detalles para la relación con ID: " + relationshipId);
            }
            return adoptionDetailMapper.toDto(detail);
        }

        // ---------- ACTUALIZACIÓN CONTROLADA ----------

        // Actualiza los campos informativos de seguimiento de un registro existente
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
