package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import com.example.cafe_con_huellas.repository.PetVaccineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Servicio encargado de la lógica de negocio del historial de vacunación
@Service
@RequiredArgsConstructor
public class PetVaccineService {

    private final PetVaccineRepository petVaccineRepository;

    // ---------- CRUD BÁSICO ----------

    // Devuelve todas las vacunas aplicadas a mascotas
    public List<PetVaccine> findAll() {
        return petVaccineRepository.findAll();
    }

    // Busca una aplicación de vacuna por su ID
    public PetVaccine findById(Long id) {
        return petVaccineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de vacunación no encontrado con ID: " + id));
    }

    // Registra una nueva vacuna aplicada
    public PetVaccine save(PetVaccine petVaccine) {
        // Validación: La fecha de aplicación no debería ser futura
        if (petVaccine.getDateAdministered() != null && petVaccine.getDateAdministered().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha de administración de la vacuna no puede ser una fecha futura.");
        }
        return petVaccineRepository.save(petVaccine);
    }

    // Elimina un registro de vacunación
    public void deleteById(Long id) {
        if (!petVaccineRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Registro no encontrado");
        }
        petVaccineRepository.deleteById(id);
    }


    // ---------- FILTROS Y BÚSQUEDAS ----------

    // Devuelve el historial de vacunación de una mascota concreta
    public List<PetVaccine> findByPetId(Long petId) {
        return petVaccineRepository.findByPetId(petId);
    }

    // Devuelve todas las aplicaciones de una vacuna concreta
    public List<PetVaccine> findByVaccineId(Long vaccineId) {
        return petVaccineRepository.findByVaccineId(vaccineId);
    }

    // Devuelve vacunas con próxima dosis pendiente a partir de una fecha
    public List<PetVaccine> findUpcomingVaccines(LocalDate fromDate) {
        return petVaccineRepository.findByNextDoseDateAfter(fromDate);
    }

    // Devuelve vacunas cuyo refuerzo ya debería haberse aplicado (vacunas vencidas)
    public List<PetVaccine> findOverdueVaccines() {
        return petVaccineRepository.findByNextDoseDateBefore(LocalDate.now());
    }


    // ---------- ACTUALIZACIÓN CONTROLADA ----------

    // Actualiza datos médicos básicos sin modificar relaciones
    public PetVaccine updateMedicalInfo(Long id, PetVaccine updated) {
        PetVaccine vaccine = findById(id); // Usa el método que ya lanza la excepción

        vaccine.setNextDoseDate(updated.getNextDoseDate());
        vaccine.setNotes(updated.getNotes());

        return petVaccineRepository.save(vaccine);
    }



}
