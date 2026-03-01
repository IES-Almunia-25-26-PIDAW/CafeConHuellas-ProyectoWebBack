package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.mapper.PetVaccineMapper;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import com.example.cafe_con_huellas.service.PetVaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Controlador para el seguimiento del calendario de vacunación.
 * Permite consultar qué vacunas tiene cada mascota.
 */
@RestController
@RequestMapping("/api/pet-vaccines")
@RequiredArgsConstructor
public class PetVaccineController {

    private final PetVaccineService petVaccineService;

    // Obtiene el historial sanitario completo de una mascota por su ID
    @GetMapping("/pet/{petId}")
    public List<PetVaccineDTO> getVaccinesByPet(@PathVariable Long petId) {
        // El servicio ya devuelve la lista de DTOs mapeada
        return petVaccineService.findByPetId(petId);
    }

    // Obtiene un registro de vacunación específico por su identificador
    @GetMapping("/{id}")
    public PetVaccineDTO getVaccineById(@PathVariable Long id) {
        return petVaccineService.findById(id);
    }

    /** * Registra la administración de una nueva vacuna.
     * Vincula una mascota con una vacuna específica y la fecha de aplicación.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetVaccineDTO addVaccineToPet(@Valid @RequestBody PetVaccineDTO dto) {
        // Pasamos el DTO directamente, el servicio se encarga de las validaciones y el mapeo
        return petVaccineService.save(dto);
    }

    /**
     * Actualiza la información médica de un registro de vacunación (notas o próxima dosis).
     */
    @PutMapping("/{id}")
    public PetVaccineDTO updateVaccineRecord(@PathVariable Long id, @Valid @RequestBody PetVaccineDTO dto) {
        return petVaccineService.updateMedicalInfo(id, dto);
    }

    /**
     * Elimina un registro de vacunación por su ID del historial de la mascota.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVaccineRecord(@PathVariable Long id) {
        petVaccineService.deleteById(id);
    }

}