package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.mapper.VaccineMapper;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import com.example.cafe_con_huellas.service.VaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* Controlador REST para la gestión del catálogo de vacunas
 * Permite definir qué vacunas existen en el sistema
 */
@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService vaccineService;
    private final VaccineMapper vaccineMapper;

    // Obtiene el catálogo completo de vacunas disponibles
    @GetMapping
    public List<VaccineDTO> getAllVaccines() {
        return vaccineService.findAll().stream()
                .map(vaccineMapper::toDto)
                .toList();
    }

    // Obtiene los detalles de una vacuna específica por su ID
    @GetMapping("/{id}")
    public VaccineDTO getVaccineById(@PathVariable Long id) {
        return vaccineMapper.toDto(vaccineService.findById(id));
    }

    /* Crea una nueva vacuna en el catálogo.
     * Útil para cuando el refugio empieza a usar una vacuna nueva.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VaccineDTO createVaccine(@Valid @RequestBody VaccineDTO vaccineDTO) {
        Vaccine vaccine = vaccineMapper.toEntity(vaccineDTO);
        return vaccineMapper.toDto(vaccineService.save(vaccine));
    }

    // Elimina una vacuna del catálogo (solo si no está siendo usada por ninguna mascota)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVaccine(@PathVariable Long id) {
        vaccineService.deleteById(id);
    }
}