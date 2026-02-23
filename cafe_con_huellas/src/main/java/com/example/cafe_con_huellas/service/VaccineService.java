package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import com.example.cafe_con_huellas.repository.VaccineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio encargado de la lógica de negocio del catálogo de vacunas
@Service
@RequiredArgsConstructor
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    // ---------------- CRUD BÁSICO ----------------

    // Obtiene todas las vacunas disponibles en el sistema
    public List<Vaccine> findAll() {
        return vaccineRepository.findAll();
    }

    // Busca una vacuna por su ID
    public Vaccine findById(Long id) {
        return vaccineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna no encontrada con ID: " + id));
    }

    // Crea o actualiza una vacuna
    public Vaccine save(Vaccine vaccine) {
        // Validación: Evitamos duplicar nombres en el catálogo
        // Si es una creación (id null) y el nombre ya existe, lanzamos error 400
        if (vaccine.getId() == null && vaccineRepository.existsByName(vaccine.getName())) {
            throw new BadRequestException("La vacuna '" + vaccine.getName() + "' ya existe en el catálogo.");
        }
        return vaccineRepository.save(vaccine);
    }

    // Elimina una vacuna del catálogo
    public void deleteById(Long id) {
        if (!vaccineRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Vacuna no encontrada.");
        }
        // Si una mascota ya tiene esta vacuna, Spring lanzará una DataIntegrityViolationException que GlobalExceptionHandler capturará automáticamente.
        vaccineRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Busca una vacuna por su nombre
    public Vaccine findByName(String name) {
        return vaccineRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna no encontrada con nombre: " + name));
    }

    // Comprueba si una vacuna ya existe por nombre
    public boolean existsByName(String name) {
        return vaccineRepository.existsByName(name);
    }


}
