package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.mapper.PetVaccineMapper;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.service.PetVaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el seguimiento del calendario de vacunación de las mascotas.
 * <p>
 * Permite consultar el historial sanitario de cada animal y gestionar
 * los registros de vacunas administradas.
 * La consulta es pública; el alta, edición y borrado requieren rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/pet-vaccines")
@RequiredArgsConstructor
public class PetVaccineController {

    private final PetVaccineService petVaccineService;

    /**
     * Obtiene el historial completo de vacunación de una mascota.
     *
     * @param petId identificador de la mascota
     * @return lista de {@link PetVaccineDTO} con todas las vacunas administradas
     */
    @GetMapping("/pet/{petId}")
    public List<PetVaccineDTO> getVaccinesByPet(@PathVariable Long petId) {
        // El servicio ya devuelve la lista de DTOs mapeada
        return petVaccineService.findByPetId(petId);
    }

    /**
     * Obtiene un registro de vacunación específico por su identificador.
     *
     * @param id identificador único del registro de vacunación
     * @return {@link PetVaccineDTO} con los datos del registro
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @GetMapping("/{id}")
    public PetVaccineDTO getVaccineById(@PathVariable Long id) {
        return petVaccineService.findById(id);
    }

    /**
     * Registra la administración de una nueva vacuna a una mascota.
     * <p>
     * Vincula una mascota con una vacuna específica e incluye la fecha de aplicación.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param dto datos del registro de vacunación a crear
     * @return {@link PetVaccineDTO} con el registro persistido
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PetVaccineDTO addVaccineToPet(@Valid @RequestBody PetVaccineDTO dto) {
        // Pasamos el DTO directamente, el servicio se encarga de las validaciones y el mapeo
        return petVaccineService.save(dto);
    }

    /**
     * Actualiza la información médica de un registro de vacunación.
     * <p>
     * Permite modificar las notas clínicas o la fecha de la próxima dosis.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param id  identificador del registro a actualizar
     * @param dto nuevos datos médicos del registro
     * @return {@link PetVaccineDTO} con los datos actualizados
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PetVaccineDTO updateVaccineRecord(@PathVariable Long id, @Valid @RequestBody PetVaccineDTO dto) {
        return petVaccineService.updateMedicalInfo(id, dto);
    }

    /**
     * Elimina un registro de vacunación del historial de la mascota.
     * Requiere rol ADMIN.
     *
     * @param id identificador del registro a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void removeVaccineRecord(@PathVariable Long id) {
        petVaccineService.deleteById(id);
    }

}