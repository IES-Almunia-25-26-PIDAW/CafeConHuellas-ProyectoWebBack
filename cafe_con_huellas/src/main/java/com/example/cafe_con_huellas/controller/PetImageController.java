package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.mapper.PetImageMapper;
import com.example.cafe_con_huellas.model.entity.PetImage;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.service.PetImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar la galería de imágenes de las mascotas.
 * <p>
 * Permite tener múltiples fotos por cada animal.
 * La consulta es pública; añadir y eliminar imágenes requiere rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/pet-images")
@RequiredArgsConstructor
public class PetImageController {

    private final PetImageService petImageService;

    /**
     * Obtiene todas las imágenes registradas en el sistema.
     *
     * @return lista de {@link PetImageDTO} con todas las imágenes
     */
    @GetMapping
    public List<PetImageDTO> getAllImages() {
        return petImageService.findAll();
    }

    /**
     * Obtiene una imagen específica por su identificador.
     *
     * @param id identificador único de la imagen
     * @return {@link PetImageDTO} con los datos de la imagen
     * @throws ResourceNotFoundException si no existe imagen con ese ID
     */
    @GetMapping("/{id}")
    public PetImageDTO getImageById(@PathVariable Long id) {
        return petImageService.findById(id);
    }

    /**
     * Obtiene todas las fotos pertenecientes a una mascota específica.
     *
     * @param petId identificador de la mascota
     * @return lista de {@link PetImageDTO} asociadas a la mascota
     */
    @GetMapping("/pet/{petId}")
    public List<PetImageDTO> getImagesByPet(@PathVariable Long petId) {
        // El servicio ya devuelve la lista de DTOs mapeada
        return petImageService.findByPetId(petId);
    }

    /**
     * Registra una nueva foto en la galería de una mascota.
     * <p>
     * Recibe la URL de la imagen y el identificador de la mascota dentro del DTO.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param dto datos de la imagen a registrar
     * @return {@link PetImageDTO} con el registro persistido
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PetImageDTO addImage(@Valid @RequestBody PetImageDTO dto) {
        // Delegamos la validación de la mascota y el mapeo al Service
        return petImageService.save(dto);
    }


    /**
     * Elimina una imagen concreta por su identificador.
     * Requiere rol ADMIN.
     *
     * @param id identificador de la imagen a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(@PathVariable Long id) {
        petImageService.deleteById(id);
    }

    /**
     * Elimina todas las imágenes asociadas a una mascota de forma masiva.
     * Requiere rol ADMIN.
     *
     * @param petId identificador de la mascota cuyas imágenes se eliminarán
     */
    @DeleteMapping("/pet/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByPetId(@PathVariable Long petId) {
        petImageService.deleteByPetId(petId);
    }
}