package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.mapper.VaccineMapper;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import com.example.cafe_con_huellas.service.VaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // Obtiene el catálogo completo de vacunas disponibles en formato DTO
    @GetMapping
    public List<VaccineDTO> getAllVaccines() {
        // El service ya devuelve la lista mapeada a DTO
        return vaccineService.findAll();
    }

    // Obtiene los detalles de una vacuna específica por su ID
    @GetMapping("/{id}")
    public VaccineDTO getVaccineById(@PathVariable Long id) {
        return vaccineService.findById(id);
    }


    // Solo ADMIN puede añadir nuevas vacunas al catálogo
    // El service valida que el nombre no esté duplicado
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public VaccineDTO createVaccine(@Valid @RequestBody VaccineDTO vaccineDTO) {
        // Pasamos el DTO directo al service para que gestione el mapeo y persistencia
        return vaccineService.save(vaccineDTO);
    }


    // Solo ADMIN puede eliminar una vacuna del catálogo
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVaccine(@PathVariable Long id) {
        vaccineService.deleteById(id);
    }
}