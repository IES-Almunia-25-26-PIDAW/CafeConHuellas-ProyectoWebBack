package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.dto.PetSummaryDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controlador REST para la gestión de mascotas
 * Incluye CRUD completo y filtros por categoría/esterilización
 */
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Validated // Permite que las validaciones en los parámetros de los métodos funcionen
public class PetController {

    private final PetService petService;
    private final PetMapper petMapper;

    // -------------------- CRUD BÁSICO --------------------

    // Obtiene el catálogo completo de mascotas en formato resumido
    @GetMapping
    public List<PetSummaryDTO> getAllPets() {
        // Obtenemos los detalles del servicio y los convertimos a resumen para la galería principal
        return petService.findAll().stream()
                .map(petMapper::toSummaryDto)
                .toList();
    }

    // Obtiene la ficha detallada de una mascota específica por su ID
    @GetMapping("/{id}")
    public PetDetailDTO getPetById(@PathVariable @NotNull Long id) {
        return petService.findById(id);
    }

    // Registra una nueva mascota en el sistema
    // Solo ADMIN puede crear mascotas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PetDetailDTO createPet(@Valid @RequestBody PetDetailDTO petDetailDTO) {
        return petService.save(petDetailDTO);
    }

    // Actualiza la información técnica y descriptiva de una mascota existente
    // Solo ADMIN puede actualizar mascotas
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PetDetailDTO updatePet(@PathVariable @NotNull Long id, @Valid @RequestBody PetDetailDTO petDetailDTO) {
        return petService.updateBasicInfo(id, petDetailDTO);
    }

    // Elimina el registro de una mascota del sistema
    // Solo ADMIN puede eliminar mascotas
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePet(@PathVariable @NotNull Long id) {
        petService.deleteById(id);
    }

    // -------------------- FILTROS --------------------

    // Filtra el catálogo según el estado de esterilización
    @GetMapping("/filter/neutered")
    public List<PetSummaryDTO> getPetsByNeutered(@RequestParam Boolean neutered) {
        return petService.findByNeutered(neutered).stream()
                .map(petMapper::toSummaryDto)
                .toList();
    }

    // Filtra el catálogo por categoría (PERRO, GATO)
    @GetMapping("/filter/category")
    public List<PetSummaryDTO> getPetsByCategory(@RequestParam String category) {
        return petService.findByCategory(category).stream()
                .map(petMapper::toSummaryDto)
                .toList();
    }

}