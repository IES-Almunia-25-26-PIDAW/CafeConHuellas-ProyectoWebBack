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

/**
 * Servicio encargado de la gestión de la galería de imágenes de las mascotas.
 * <p>
 * Permite añadir, consultar y eliminar fotos asociadas a cada animal,
 * validando que la mascota referenciada exista antes de persistir.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PetImageService {

    private final PetImageRepository petImageRepository;
    private final PetRepository petRepository;
    private final PetImageMapper petImageMapper;

    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todas las imágenes registradas en el sistema.
     *
     * @return lista de {@link PetImageDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<PetImageDTO> findAll() {
        return petImageRepository.findAll().stream()
                .map(petImageMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca una imagen por su identificador.
     *
     * @param id identificador único de la imagen
     * @return {@link PetImageDTO} con los datos de la imagen
     * @throws ResourceNotFoundException si no existe la imagen con ese ID
     */
    @Transactional(readOnly = true)
    public PetImageDTO findById(Long id) {
        return petImageRepository.findById(id)
                .map(petImageMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen de la mascota no encontrada con ID: " + id));
    }

    /**
     * Registra una nueva imagen en la galería de una mascota.
     * <p>
     * Valida que la mascota exista antes de persistir la imagen.
     * </p>
     *
     * @param dto datos de la imagen a registrar, incluyendo la URL y el ID de la mascota
     * @return {@link PetImageDTO} con el registro persistido
     * @throws ResourceNotFoundException si la mascota referenciada no existe
     */
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

    /**
     * Elimina una imagen concreta por su identificador.
     *
     * @param id identificador de la imagen a eliminar
     * @throws ResourceNotFoundException si no existe la imagen con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!petImageRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. La imagen con ID " + id + " no existe.");
        }
        petImageRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    /**
     * Obtiene todas las imágenes asociadas a una mascota específica.
     *
     * @param petId identificador de la mascota
     * @return lista de {@link PetImageDTO} de la mascota indicada
     */
    @Transactional(readOnly = true)
    public List<PetImageDTO> findByPetId(Long petId) {
        return petImageRepository.findByPetId(petId).stream()
                .map(petImageMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Elimina todas las imágenes asociadas a una mascota de forma masiva.
     *
     * @param petId identificador de la mascota cuyas imágenes se eliminarán
     */
    @Transactional
    public void deleteByPetId(Long petId) {
        // Verificamos si la mascota tiene imágenes antes de proceder al borrado masivo
        List<PetImage> images = petImageRepository.findByPetId(petId);
        if (!images.isEmpty()) {
            petImageRepository.deleteByPetId(petId);
        }
    }

}
