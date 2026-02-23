package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.mapper.PetImageMapper;
import com.example.cafe_con_huellas.model.entity.PetImage;
import com.example.cafe_con_huellas.service.PetImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final PetImageMapper petImageMapper;

    // Obtiene todas las imágenes de la galería de una mascota específica
    @GetMapping("/pet/{petId}")
    public List<PetImageDTO> getImagesByPet(@PathVariable Long petId) {
        return petImageService.findByPetId(petId).stream()
                .map(petImageMapper::toDto)
                .toList();
    }

    // Añade una nueva foto a la galería de una mascota
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetImageDTO addImage(@Valid @RequestBody PetImageDTO dto) {
        PetImage entity = petImageMapper.toEntity(dto);
        return petImageMapper.toDto(petImageService.save(entity));
    }

    // Borra una foto específica de la galería
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Long id) {
        petImageService.deleteById(id);
    }
}