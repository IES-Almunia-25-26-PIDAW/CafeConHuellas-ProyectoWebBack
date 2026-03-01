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

// Servicio encargado de la gestión de las mascotas favoritas de los usuarios
@Service
@RequiredArgsConstructor
public class UserPetFavoriteService {

    private final UserPetFavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserPetFavoriteMapper favoriteMapper;

    // ---------- CRUD BÁSICO ----------

    // Obtiene todos los registros de favoritos del sistema convertidos a DTO
    @Transactional(readOnly = true)
    public List<UserPetFavoriteDTO> findAll() {
        return favoriteRepository.findAll().stream()
                .map(favoriteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca un registro de favorito específico por su ID y lo devuelve como DTO
    @Transactional(readOnly = true)
    public UserPetFavoriteDTO findById(Long id) {
        return favoriteRepository.findById(id)
                .map(favoriteMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota Favorita no encontrada con ID: " + id));
    }

    // Registra una nueva mascota en la lista de favoritos de un usuario
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

    // Elimina un registro de favorito mediante su identificador único
    @Transactional
    public void deleteById(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se pudo eliminar. El registro de favorito no existe.");
        }
        favoriteRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Recupera todas las mascotas marcadas como favoritas por un usuario concreto
    @Transactional(readOnly = true)
    public List<UserPetFavoriteDTO> findByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favoriteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Verifica si existe una relación de favorito activa entre un usuario y una mascota
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long petId) {
        return favoriteRepository.existsByUserIdAndPetId(userId, petId);
    }

    // Elimina la marca de favorito de una mascota para un usuario específico (Unfavorite)
    @Transactional
    public void removeFavorite(Long userId, Long petId) {
        // Verificamos existencia antes de intentar borrar por parámetros
        if (!favoriteRepository.existsByUserIdAndPetId(userId, petId)) {
            throw new ResourceNotFoundException("No existe el favorito para este usuario y mascota.");
        }
        favoriteRepository.deleteByUserIdAndPetId(userId, petId);
    }


}
