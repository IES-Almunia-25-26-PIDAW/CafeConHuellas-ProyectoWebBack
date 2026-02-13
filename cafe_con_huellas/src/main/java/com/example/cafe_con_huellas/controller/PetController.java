package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.dto.PetSummaryDTO;
import com.example.cafe_con_huellas.mapper.PetMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.service.PetService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controlador REST para la gestión de mascotas
 * Incluye CRUD completo y filtros por categoría/esterilización
 */
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
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
    public ResponseEntity<Void> deletePet(@PathVariable @NotNull Long id) {
        petService.deleteById(id);
        return ResponseEntity.noContent().build();
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
        return petService.findByCategory(category)
                .stream()
                .map(petMapper::toSummaryDto)
                .toList();
    }

    // -------------------- MANEJO DE ERRORES --------------------

    /**
     * Captura excepciones cuando no se encuentra un recurso
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    /**
     * Captura errores de validación
     */
    @ExceptionHandler({IllegalArgumentException.class, jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }


}
