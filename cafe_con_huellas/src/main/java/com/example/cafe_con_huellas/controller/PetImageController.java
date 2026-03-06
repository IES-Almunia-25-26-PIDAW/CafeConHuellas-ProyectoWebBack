package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.mapper.PetImageMapper;
import com.example.cafe_con_huellas.model.entity.PetImage;
import com.example.cafe_con_huellas.service.PetImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* Controlador para gestionar la galería de fotos de las mascotas.
 * Permite tener múltiples imágenes por cada animal.
 */
@RestController
@RequestMapping("/api/pet-images")
@RequiredArgsConstructor
public class PetImageController {

    private final PetImageService petImageService;

    // Obtiene todas las imágenes registradas en el sistema
    @GetMapping
    public List<PetImageDTO> getAllImages() {
        return petImageService.findAll();
    }

    // Busca una imagen específica mediante su identificador único
    @GetMapping("/{id}")
    public PetImageDTO getImageById(@PathVariable Long id) {
        return petImageService.findById(id);
    }

    // Recupera todas las fotos pertenecientes a una mascota específica
    @GetMapping("/pet/{petId}")
    public List<PetImageDTO> getImagesByPet(@PathVariable Long petId) {
        // El servicio ya devuelve la lista de DTOs mapeada
        return petImageService.findByPetId(petId);
    }

    /* * Registra una nueva foto en la galería de una mascota.
     * Recibe la URL y el ID de la mascota dentro del DTO.
     */
    // Solo ADMIN puede añadir fotos a una mascota
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PetImageDTO addImage(@Valid @RequestBody PetImageDTO dto) {
        // Delegamos la validación de la mascota y el mapeo al Service
        return petImageService.save(dto);
    }


    // Solo ADMIN puede eliminar una foto concreta
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(@PathVariable Long id) {
        petImageService.deleteById(id);
    }

    // Elimina todas las fotos asociadas a una mascota de forma masiva
    // Solo ADMIN puede eliminar todas las fotos de una mascota
    @DeleteMapping("/pet/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByPetId(@PathVariable Long petId) {
        petImageService.deleteByPetId(petId);
    }
}