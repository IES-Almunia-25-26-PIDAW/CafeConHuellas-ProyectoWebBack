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
    private final PetVaccineMapper petVaccineMapper;

    // Obtiene el historial sanitario completo de una mascota por su ID
    @GetMapping("/pet/{petId}")
    public List<PetVaccineDTO> getVaccinesByPet(@PathVariable Long petId) {
        return petVaccineService.findByPetId(petId).stream()
                .map(petVaccineMapper::toDto)
                .toList();
    }

    /** Registra la administración de una nueva vacuna.
     * Vincula una mascota con una vacuna específica y la fecha de aplicación.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetVaccineDTO addVaccineToPet(@Valid @RequestBody PetVaccineDTO dto) {
        PetVaccine entity = petVaccineMapper.toEntity(dto);
        return petVaccineMapper.toDto(petVaccineService.save(entity));
    }

    /**
     * Elimina un registro de vacunación por su ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVaccineRecord(@PathVariable Long id) {
        petVaccineService.deleteById(id);
    }

}