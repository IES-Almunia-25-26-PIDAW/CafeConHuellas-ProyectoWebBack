package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.VaccineMapper;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import com.example.cafe_con_huellas.repository.VaccineRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Servicio encargado de la lógica de negocio del catálogo de vacunas
@Service
@RequiredArgsConstructor
public class VaccineService {

    private final VaccineRepository vaccineRepository;
    private final VaccineMapper vaccineMapper;

    // ---------------- CRUD BÁSICO ----------------

    // Obtiene todas las vacunas del catálogo convertidas a DTO
    @Transactional(readOnly = true)
    public List<VaccineDTO> findAll() {
        return vaccineRepository.findAll().stream()
                .map(vaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca una vacuna específica por su ID
    @Transactional(readOnly = true)
    public VaccineDTO findById(Long id) {
        return vaccineRepository.findById(id)
                .map(vaccineMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna no encontrada con ID: " + id));
    }

    // Registra una nueva vacuna en el catálogo validando que no esté duplicada
    @Transactional
    public VaccineDTO save(VaccineDTO dto) {
        // Validación: Evitamos duplicar nombres en el catálogo
        if (dto.getId() == null && vaccineRepository.existsByName(dto.getName())) {
            throw new BadRequestException("La vacuna '" + dto.getName() + "' ya existe en el catálogo.");
        }

        Vaccine vaccine = vaccineMapper.toEntity(dto);
        return vaccineMapper.toDto(vaccineRepository.save(vaccine));
    }

    // Elimina una vacuna del catálogo
    @Transactional
    public void deleteById(Long id) {
        if (!vaccineRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Vacuna no encontrada.");
        }
        vaccineRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Busca una vacuna por su nombre exacto
    @Transactional(readOnly = true)
    public VaccineDTO findByName(String name) {
        return vaccineRepository.findByName(name)
                .map(vaccineMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna no encontrada con nombre: " + name));
    }

    // Comprueba la existencia de una vacuna antes de crear registros sanitarios
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return vaccineRepository.existsByName(name);
    }

}
