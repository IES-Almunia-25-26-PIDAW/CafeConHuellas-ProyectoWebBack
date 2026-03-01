package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetImageMapper;
import com.example.cafe_con_huellas.model.entity.PetImage;
import com.example.cafe_con_huellas.repository.PetImageRepository;
import com.example.cafe_con_huellas.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio encargado de la gestión de la galería de imágenes de las mascotas
@Service
@RequiredArgsConstructor
public class PetImageService {

    private final PetImageRepository petImageRepository;
    private final PetRepository petRepository;
    private final PetImageMapper petImageMapper;

    // ---------- CRUD BÁSICO ----------

    // Obtiene el listado completo de imágenes registradas convertido a DTO
    @Transactional(readOnly = true)
    public List<PetImageDTO> findAll() {
        return petImageRepository.findAll().stream()
                .map(petImageMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca una imagen específica por su ID y la devuelve como DTO
    @Transactional(readOnly = true)
    public PetImageDTO findById(Long id) {
        return petImageRepository.findById(id)
                .map(petImageMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen de la mascota no encontrada con ID: " + id));
    }

    // Registra una nueva imagen vinculándola obligatoriamente a una mascota existente
    @Transactional
    public PetImageDTO save(PetImageDTO dto) {
        // Convertimos el DTO a Entidad para trabajar con la persistencia
        PetImage petImage = petImageMapper.toEntity(dto);

        // Verificamos que la mascota asociada exista realmente en la base de datos
        petImage.setPet(petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede guardar la imagen. Mascota no encontrada")));

        // Guardamos y retornamos el resultado mapeado a DTO
        return petImageMapper.toDto(petImageRepository.save(petImage));
    }

    // Elimina una imagen específica del sistema validando su existencia previa
    @Transactional
    public void deleteById(Long id) {
        if (!petImageRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. La imagen con ID " + id + " no existe.");
        }
        petImageRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Recupera todas las imágenes pertenecientes a una mascota concreta
    @Transactional(readOnly = true)
    public List<PetImageDTO> findByPetId(Long petId) {
        return petImageRepository.findByPetId(petId).stream()
                .map(petImageMapper::toDto)
                .collect(Collectors.toList());
    }

    // Elimina de forma masiva todas las fotos asociadas a una mascota (ej. al darla de baja)
    @Transactional
    public void deleteByPetId(Long petId) {
        // Verificamos si la mascota tiene imágenes antes de proceder al borrado masivo
        List<PetImage> images = petImageRepository.findByPetId(petId);
        if (!images.isEmpty()) {
            petImageRepository.deleteByPetId(petId);
        }
    }

}
