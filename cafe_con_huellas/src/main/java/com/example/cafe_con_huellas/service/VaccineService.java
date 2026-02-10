package com.example.cafe_con_huellas.service;

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
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));
    }

    // Crea o actualiza una vacuna
    public Vaccine save(Vaccine vaccine) {
        return vaccineRepository.save(vaccine);
    }

    // Elimina una vacuna del catálogo
    public void deleteById(Long id) {
        vaccineRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Busca una vacuna por su nombre
    public Vaccine findByName(String name) {
        return vaccineRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));
    }

    // Comprueba si una vacuna ya existe por nombre
    public boolean existsByName(String name) {
        return vaccineRepository.existsByName(name);
    }


}
