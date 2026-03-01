package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetVaccineMapper;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.PetVaccineRepository;
import com.example.cafe_con_huellas.repository.VaccineRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Servicio encargado de la lógica de negocio del historial de vacunación
@Service
@RequiredArgsConstructor
public class PetVaccineService {

    private final PetVaccineRepository petVaccineRepository;
    private final PetRepository petRepository;
    private final VaccineRepository vaccineRepository;
    private final PetVaccineMapper petVaccineMapper;

    // ---------- CRUD BÁSICO ----------

    // Obtiene todo el historial de vacunación global convertido a DTO
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findAll() {
        return petVaccineRepository.findAll().stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca un registro de vacunación específico por su ID y lo devuelve como DTO
    @Transactional(readOnly = true)
    public PetVaccineDTO findById(Long id) {
        return petVaccineRepository.findById(id)
                .map(petVaccineMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de vacunación no encontrado con ID: " + id));
    }

    // Registra la aplicación de una vacuna validando existencia de mascota y tipo de vacuna
    @Transactional
    public PetVaccineDTO save(PetVaccineDTO dto) {
        // Validación de negocio: La vacuna ya debe haberse puesto, no puede ser en el futuro
        if (dto.getDateAdministered() != null && dto.getDateAdministered().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha de administración no puede ser una fecha futura.");
        }

        // Convertimos el DTO a Entidad
        PetVaccine petVaccine = petVaccineMapper.toEntity(dto);

        // Vinculamos con las entidades reales de la base de datos
        petVaccine.setPet(petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + dto.getPetId())));

        petVaccine.setVaccine(vaccineRepository.findById(dto.getVaccineId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de vacuna no encontrado con ID: " + dto.getVaccineId())));

        // Guardamos y retornamos el DTO
        return petVaccineMapper.toDto(petVaccineRepository.save(petVaccine));
    }

    // Elimina un registro de vacunación del historial médico
    @Transactional
    public void deleteById(Long id) {
        if (!petVaccineRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Registro no encontrado");
        }
        petVaccineRepository.deleteById(id);
    }

    // ---------- FILTROS Y BÚSQUEDAS ----------

    // Obtiene el historial completo de vacunas de una mascota específica
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findByPetId(Long petId) {
        return petVaccineRepository.findByPetId(petId).stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca vacunas programadas para refuerzo a partir de una fecha concreta
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findUpcomingVaccines(LocalDate fromDate) {
        return petVaccineRepository.findByNextDoseDateAfter(fromDate).stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    // Identifica vacunas cuyo refuerzo ha expirado (pendientes de aplicar)
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findOverdueVaccines() {
        return petVaccineRepository.findByNextDoseDateBefore(LocalDate.now()).stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    // ---------- ACTUALIZACIÓN CONTROLADA ----------

    // Actualiza información médica como la fecha de la próxima dosis o notas de observación
    @Transactional
    public PetVaccineDTO updateMedicalInfo(Long id, PetVaccineDTO dto) {
        // Verificamos que el registro exista
        PetVaccine vaccine = petVaccineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Registro no encontrado"));

        // Actualizamos campos permitidos para no romper la trazabilidad del tratamiento
        vaccine.setNextDoseDate(dto.getNextDoseDate());
        vaccine.setNotes(dto.getNotes());

        return petVaccineMapper.toDto(petVaccineRepository.save(vaccine));
    }



}
