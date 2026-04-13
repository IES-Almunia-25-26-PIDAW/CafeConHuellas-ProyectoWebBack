package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.mapper.PetMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import com.example.cafe_con_huellas.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Servicio encargado de la lógica de negocio del catálogo de mascotas.
 * <p>
 * Gestiona el ciclo de vida completo de las mascotas del refugio,
 * incluyendo filtros por categoría, esterilización y búsqueda por texto.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todas las mascotas registradas convertidas a DTO detallado.
     *
     * @return lista de {@link PetDetailDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<PetDetailDTO> findAll() {
        return petRepository.findAll().stream()
                .map(petMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca una mascota por su identificador.
     *
     * @param id identificador único de la mascota
     * @return {@link PetDetailDTO} con la ficha completa de la mascota
     * @throws ResourceNotFoundException si no existe la mascota con ese ID
     */
    @Transactional(readOnly = true)
    public PetDetailDTO findById(Long id) {
        return petRepository.findById(id)
                .map(petMapper::toDetailDto)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));
    }

    /**
     * Registra una nueva mascota en el sistema.
     *
     * @param dto datos de la mascota a registrar
     * @return {@link PetDetailDTO} con la mascota persistida
     */
    @Transactional
    public PetDetailDTO save(PetDetailDTO dto) {
        // Convertimos el DTO de detalle a la entidad Pet
        Pet pet = petMapper.toEntity(dto);

        // Guardamos la mascota (JPA se encarga de las imágenes si está configurado en Cascade)
        Pet savedPet = petRepository.save(pet);

        return petMapper.toDetailDto(savedPet);
    }

    /**
     * Elimina el registro de una mascota por su identificador.
     *
     * @param id identificador de la mascota a eliminar
     * @throws ResourceNotFoundException si no existe la mascota con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Mascota no encontrada");
        }
        petRepository.deleteById(id);
    }

    // ---------- FILTROS Y BÚSQUEDAS ----------

    /**
     * Filtra las mascotas por su estado de esterilización.
     *
     * @param neutered {@code true} para mascotas esterilizadas, {@code false} para no esterilizadas
     * @return lista de {@link PetDetailDTO} filtrada por estado de esterilización
     */
    @Transactional(readOnly = true)
    public List<PetDetailDTO> findByNeutered(Boolean neutered) {
        return petRepository.findByNeutered(neutered).stream()
                .map(petMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Filtra las mascotas por categoría.
     * <p>
     * Convierte el {@code String} recibido al enum {@link PetCategory}.
     * </p>
     *
     * @param categoryName nombre de la categoría en texto (insensible a mayúsculas)
     * @return lista de {@link PetDetailDTO} de la categoría indicada
     * @throws BadRequestException si la categoría no es válida
     */
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

    /**
     * Busca mascotas por nombre o raza con coincidencia parcial e insensible a mayúsculas.
     *
     * @param text texto a buscar en el nombre o la raza
     * @return lista de {@link PetDetailDTO} que coinciden con la búsqueda
     */
    @Transactional(readOnly = true)
    public List<PetDetailDTO> search(String text) {
        return petRepository.findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(text, text)
                .stream()
                .map(petMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Filtra las mascotas por urgencia de adopción.
     *
     * @param urgentAdoption {@code true} para adopciones urgentes
     * @return lista de {@link PetDetailDTO} filtrada por urgencia
     */
    @Transactional(readOnly = true)
    public List<PetDetailDTO> findByUrgentAdoption(Boolean urgentAdoption) {
        return petRepository.findByUrgentAdoption(urgentAdoption).stream()
                .map(petMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    // ---------- ACTUALIZACIÓN CONTROLADA ----------

    /**
     * Actualiza la información técnica y descriptiva de una mascota existente.
     * <p>
     * Solo modifica los campos editables: nombre, descripción, raza, edad, peso,
     * esterilización, PPP, categoría e imagen principal.
     * </p>
     *
     * @param id  identificador de la mascota a actualizar
     * @param dto nuevos datos de la mascota
     * @return {@link PetDetailDTO} con los datos actualizados
     * @throws ResourceNotFoundException si no existe la mascota con ese ID
     */
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
        pet.setUrgentAdoption(dto.getUrgentAdoption());
        pet.setCategory(PetCategory.valueOf(dto.getCategory().toUpperCase()));

        // La imagen principal también es editable
        pet.setImageUrl(dto.getImageUrl());

        return petMapper.toDetailDto(petRepository.save(pet));
    }


}
