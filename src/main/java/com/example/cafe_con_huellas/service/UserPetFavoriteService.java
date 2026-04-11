package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.UserPetFavoriteDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserPetFavoriteMapper;
import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.UserPetFavoriteRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la gestión de las mascotas favoritas de los usuarios.
 * <p>
 * Permite marcar y desmarcar mascotas como favoritas, validando
 * que no existan duplicados y que las entidades referenciadas sean válidas.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserPetFavoriteService {

    private final UserPetFavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserPetFavoriteMapper favoriteMapper;

    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todos los registros de favoritos del sistema.
     *
     * @return lista de {@link UserPetFavoriteDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<UserPetFavoriteDTO> findAll() {
        return favoriteRepository.findAll().stream()
                .map(favoriteMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un registro de favorito por su identificador.
     *
     * @param id identificador único del registro
     * @return {@link UserPetFavoriteDTO} con los datos del registro
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional(readOnly = true)
    public UserPetFavoriteDTO findById(Long id) {
        return favoriteRepository.findById(id)
                .map(favoriteMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota Favorita no encontrada con ID: " + id));
    }

    /**
     * Añade una mascota a la lista de favoritos de un usuario.
     * <p>
     * Valida que no exista ya el favorito para esa combinación usuario-mascota
     * y que ambas entidades existan en el sistema.
     * </p>
     *
     * @param dto datos con el identificador del usuario y de la mascota
     * @return {@link UserPetFavoriteDTO} con el registro creado
     * @throws BadRequestException si la mascota ya está en favoritos del usuario
     * @throws ResourceNotFoundException si el usuario o la mascota no existen
     */
    @Transactional
    public UserPetFavoriteDTO save(UserPetFavoriteDTO dto) {
        // Validación de negocio: Evitamos que una mascota se guarde dos veces para el mismo usuario
        if (favoriteRepository.existsByUserIdAndPetId(dto.getUserId(), dto.getPetId())) {
            throw new BadRequestException("Esta mascota ya se encuentra en tu lista de favoritos.");
        }

        // Convertimos el DTO a Entidad
        UserPetFavorite favorite = favoriteMapper.toEntity(dto);

        // Validamos y vinculamos las entidades reales de Usuario y Mascota
        favorite.setUser(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUserId())));

        favorite.setPet(petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + dto.getPetId())));

        // Guardamos y retornamos el resultado como DTO
        return favoriteMapper.toDto(favoriteRepository.save(favorite));
    }

    /**
     * Elimina un registro de favorito por su identificador único.
     *
     * @param id identificador del registro a eliminar
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se pudo eliminar. El registro de favorito no existe.");
        }
        favoriteRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    /**
     * Obtiene todas las mascotas marcadas como favoritas por un usuario concreto.
     *
     * @param userId identificador del usuario
     * @return lista de {@link UserPetFavoriteDTO} del usuario indicado
     */
    @Transactional(readOnly = true)
    public List<UserPetFavoriteDTO> findByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favoriteMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Comprueba si existe una relación de favorito entre un usuario y una mascota.
     *
     * @param userId identificador del usuario
     * @param petId  identificador de la mascota
     * @return {@code true} si la mascota es favorita del usuario, {@code false} en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long petId) {
        return favoriteRepository.existsByUserIdAndPetId(userId, petId);
    }

    /**
     * Elimina la marca de favorito de una mascota para un usuario específico.
     *
     * @param userId identificador del usuario
     * @param petId  identificador de la mascota a desmarcar
     * @throws ResourceNotFoundException si no existe el favorito para esa combinación
     */
    @Transactional
    public void removeFavorite(Long userId, Long petId) {
        // Verificamos existencia antes de intentar borrar por parámetros
        if (!favoriteRepository.existsByUserIdAndPetId(userId, petId)) {
            throw new ResourceNotFoundException("No existe el favorito para este usuario y mascota.");
        }
        favoriteRepository.deleteByUserIdAndPetId(userId, petId);
    }


}
