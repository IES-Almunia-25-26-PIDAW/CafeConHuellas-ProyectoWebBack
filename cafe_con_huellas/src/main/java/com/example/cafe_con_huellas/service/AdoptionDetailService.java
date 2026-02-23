package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import com.example.cafe_con_huellas.repository.AdoptionDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionDetailService {

    private final AdoptionDetailRepository adoptionDetailRepository;

    // ---------- CRUD BÁSICO ----------

    // Obtener todos los detalles de adopción registrados
    public List<AdoptionDetail> findAll() {
        return adoptionDetailRepository.findAll();
    }

    // Buscar detalle por ID
    public AdoptionDetail findById(Long id) {
        return adoptionDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de adopción no encontrado con ID: " + id));
    }

    // Guardar detalle
    public AdoptionDetail save(AdoptionDetail adoptionDetail) {
        // Validación: Si ya existe un detalle para esta relación, no permitimos otro (1:1)
        if (adoptionDetail.getId() == null &&
                existsByRelationshipId(adoptionDetail.getRelationship().getId())) {
            throw new BadRequestException("Esta relación ya tiene un registro de detalles de adopción.");
        }
        return adoptionDetailRepository.save(adoptionDetail);
    }

    // Eliminar detalle
    public void deleteById(Long id) {
        if (!adoptionDetailRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Registro no encontrado.");
        }
        adoptionDetailRepository.deleteById(id);
    }


    // ---------- CONSULTAS ÚTILES ----------no

    // Obtiene el detalle de adopción asociado a una relación concreta
    public AdoptionDetail findByRelationshipId(Long relationshipId) {
        return adoptionDetailRepository.findByRelationshipId(relationshipId);
    }

    // Comprueba si una relación ya tiene detalle de adopción, evita duplicados
    public boolean existsByRelationshipId(Long relationshipId) {
        return adoptionDetailRepository.existsByRelationshipId(relationshipId);
    }



    // ---------- ACTUALIZACIÓN CONTROLADA ----------
    // Actualiza información administrativa de la adopción
    public AdoptionDetail updateDetails(Long id, AdoptionDetail updated) {
        AdoptionDetail detail = findById(id);

        detail.setPlace(updated.getPlace());
        detail.setConditions(updated.getConditions());
        detail.setIssues(updated.getIssues());
        detail.setNotes(updated.getNotes());

        return adoptionDetailRepository.save(detail);
    }



}
