package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.model.entity.PetImage;
import com.example.cafe_con_huellas.repository.PetImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio encargado de la gestión de la galería de imágenes de las mascotas
@Service
@RequiredArgsConstructor
public class PetImageService {

    private final PetImageRepository petImageRepository;

    // ---------- CRUD BÁSICO ----------

    // Devuelve todas las imágenes registradas
    public List<PetImage> findAll() {
        return petImageRepository.findAll();
    }

    // Busca una imagen por su ID
    public PetImage findById(Long id) {
        return petImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet image not found"));
    }

    // Guarda una nueva imagen o actualiza una existente
    public PetImage save(PetImage petImage) {
        return petImageRepository.save(petImage);
    }

    // Elimina una imagen por su ID
    public void deleteById(Long id) {
        petImageRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Obtiene todas las imágenes asociadas a una mascota concreta
    public List<PetImage> findByPetId(Long petId) {
        return petImageRepository.findByPetId(petId);
    }

    // Elimina todas las imágenes asociadas a una mascota
    public void deleteByPetId(Long petId) {
        petImageRepository.deleteByPetId(petId);
    }

}
