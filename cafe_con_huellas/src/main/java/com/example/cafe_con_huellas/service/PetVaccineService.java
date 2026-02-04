package com.example.cafe_con_huellas.service;

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
                .orElseThrow(() -> new RuntimeException("Pet vaccine not found"));
    }

    // Registra una nueva vacuna aplicada
    public PetVaccine save(PetVaccine petVaccine) {
        return petVaccineRepository.save(petVaccine);
    }

    // Elimina un registro de vacunación
    public void deleteById(Long id) {
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
        PetVaccine vaccine = findById(id);

        vaccine.setNextDoseDate(updated.getNextDoseDate());
        vaccine.setNotes(updated.getNotes());

        return petVaccineRepository.save(vaccine);
    }



}
