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

    /**
     * Lista todas las mascotas con información resumida
     * @return lista de PetSummaryDTO
     */
    @GetMapping
    public List<PetSummaryDTO> getAllPets() {
        return petService.findAll()
                .stream()
                .map(petMapper::toSummaryDto)
                .toList();
    }

    /**
     * Obtiene los detalles completos de una mascota por su ID
     * @param id ID de la mascota
     * @return PetDetailDTO con toda la información
     */
    @GetMapping("/{id}")
    public PetDetailDTO getPetById(@PathVariable @NotNull Long id) {
        Pet pet = petService.findById(id);
        return petMapper.toDetailDto(pet);
    }

    /**
     * Crea una nueva mascota en el sistema
     * Validación: campos obligatorios en DTO
     * @param petDetailDTO DTO con datos de la mascota
     * @return PetDetailDTO de la mascota creada
     */
    @PostMapping
    public ResponseEntity<PetDetailDTO> createPet(@Valid @RequestBody PetDetailDTO petDetailDTO) {
        Pet pet = petMapper.toEntity(petDetailDTO);
        Pet savedPet = petService.save(pet);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(petMapper.toDetailDto(savedPet));
    }

    /**
     * Actualiza los datos de una mascota existente
     * @param id ID de la mascota
     * @param petDetailDTO DTO con nuevos datos
     * @return PetDetailDTO actualizado
     */
    @PutMapping("/{id}")
    public PetDetailDTO updatePet(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody PetDetailDTO petDetailDTO) {

        Pet existingPet = petService.findById(id);

        // Actualizamos los campos editables
        existingPet.setName(petDetailDTO.getName());
        existingPet.setDescription(petDetailDTO.getDescription());
        existingPet.setBreed(petDetailDTO.getBreed());
        // Se asume que el mapper maneja la conversión de String a Enum correctamente
        existingPet.setCategory(petMapper.categoryFromString(petDetailDTO.getCategory()));
        existingPet.setAge(petDetailDTO.getAge());
        existingPet.setWeight(petDetailDTO.getWeight());
        existingPet.setNeutered(petDetailDTO.getNeutered());
        existingPet.setIsPpp(petDetailDTO.getIsPpp());
        existingPet.setImageUrl(petDetailDTO.getImageUrl());

        Pet updatedPet = petService.save(existingPet);
        return petMapper.toDetailDto(updatedPet);
    }

    /**
     * Elimina una mascota por su ID
     * @param id ID de la mascota
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable @NotNull Long id) {
        petService.deleteById(id);
    }

    // -------------------- FILTROS --------------------

    /**
     * Filtra mascotas según si están esterilizadas o no
     * @param neutered true o false
     * @return lista de PetSummaryDTO
     */
    @GetMapping("/filter/neutered")
    public List<PetSummaryDTO> getPetsByNeutered(@RequestParam Boolean neutered) {
        return petService.findByNeutered(neutered)
                .stream()
                .map(petMapper::toSummaryDto)
                .toList();
    }

    /**
     * Filtra mascotas por categoría (GATO / PERRO)
     * @param category texto de la categoría
     * @return lista de PetSummaryDTO
     */
    @GetMapping("/filter/category")
    public List<PetSummaryDTO> getPetsByCategory(@RequestParam String category) {
        // Convertimos el String a Enum usando el método del mapper
        var categoryEnum = petMapper.categoryFromString(category);

        // Pasamos el Enum al servicio que es lo que él espera
        return petService.findByCategory(categoryEnum)
                .stream()
                .map(petMapper::toSummaryDto)
                .toList();
    }


}