package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import com.example.cafe_con_huellas.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    // ---------- CRUD BÁSICO ----------

    // Obtiene todas las mascotas con su detalle completo (incluyendo galería)
    @Transactional(readOnly = true)
    public List<PetDetailDTO> findAll() {
        return petRepository.findAll().stream()
                .map(petMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    // Busca una mascota por su ID y devuelve su información detallada
    @Transactional(readOnly = true)
    public PetDetailDTO findById(Long id) {
        return petRepository.findById(id)
                .map(petMapper::toDetailDto)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));
    }

    // Registra una nueva mascota o guarda cambios profundos desde el DTO de detalle
    @Transactional
    public PetDetailDTO save(PetDetailDTO dto) {
        // Convertimos el DTO de detalle a la entidad Pet
        Pet pet = petMapper.toEntity(dto);

        // Guardamos la mascota (JPA se encarga de las imágenes si está configurado en Cascade)
        Pet savedPet = petRepository.save(pet);

        return petMapper.toDetailDto(savedPet);
    }

    // Elimina una mascota y toda su información relacionada del sistema
    @Transactional
    public void deleteById(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Mascota no encontrada");
        }
        petRepository.deleteById(id);
    }

    // ---------- FILTROS Y BÚSQUEDAS ----------

    // Filtra mascotas por su estado de esterilización devolviendo el detalle completo
    @Transactional(readOnly = true)
    public List<PetDetailDTO> findByNeutered(Boolean neutered) {
        return petRepository.findByNeutered(neutered).stream()
                .map(petMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    // Filtra mascotas por categoría (PERRO, GATO)
    @Transactional(readOnly = true)
    public List<PetDetailDTO> findByCategory(String categoryName) {
        try {
            PetCategory category = PetCategory.valueOf(categoryName.toUpperCase());
            return petRepository.findByCategory(category).stream()
                    .map(petMapper::toDetailDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new com.example.cafe_con_huellas.exception.BadRequestException("Categoría no válida: " + categoryName);
        }
    }

    // Busca mascotas por nombre o raza con coincidencia parcial
    @Transactional(readOnly = true)
    public List<PetDetailDTO> search(String text) {
        return petRepository.findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(text, text)
                .stream()
                .map(petMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    // ---------- ACTUALIZACIÓN CONTROLADA ----------

    // Actualiza la información técnica y descriptiva de una mascota existente
    @Transactional
    public PetDetailDTO updateBasicInfo(Long id, PetDetailDTO dto) {
        // Recuperamos la mascota original
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar. Mascota no encontrada"));

        // Sincronizamos los campos editables del DTO a la entidad
        pet.setName(dto.getName());
        pet.setDescription(dto.getDescription());
        pet.setBreed(dto.getBreed());
        pet.setAge(dto.getAge());
        pet.setWeight(dto.getWeight());
        pet.setNeutered(dto.getNeutered());
        pet.setIsPpp(dto.getIsPpp());
        pet.setCategory(PetCategory.valueOf(dto.getCategory().toUpperCase()));

        // La imagen principal también es editable
        pet.setImageUrl(dto.getImageUrl());

        return petMapper.toDetailDto(petRepository.save(pet));
    }


}
