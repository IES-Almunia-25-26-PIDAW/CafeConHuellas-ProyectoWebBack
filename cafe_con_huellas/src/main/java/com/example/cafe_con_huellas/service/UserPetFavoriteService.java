package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import com.example.cafe_con_huellas.repository.UserPetFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio encargado de la gestión de las mascotas favoritas de los usuarios
@Service
@RequiredArgsConstructor
public class UserPetFavoriteService {

    private final UserPetFavoriteRepository favoriteRepository;

    // ---------- CRUD BÁSICO ----------

    // Devuelve todos los registros de favoritos
    public List<UserPetFavorite> findAll() {
        return favoriteRepository.findAll();
    }

    // Busca un favorito por su ID
    public UserPetFavorite findById(Long id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
    }

    // Guarda un nuevo favorito
    public UserPetFavorite save(UserPetFavorite favorite) {
        return favoriteRepository.save(favorite);
    }

    // Elimina un favorito por su ID
    public void deleteById(Long id) {
        favoriteRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Devuelve todas las mascotas favoritas de un usuario
    public List<UserPetFavorite> findByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    // Comprueba si una mascota ya está marcada como favorita por el usuario
    public boolean isFavorite(Long userId, Long petId) {
        return favoriteRepository.existsByUserIdAndPetId(userId, petId);
    }

    // Elimina una mascota de la lista de favoritos de un usuario
    public void removeFavorite(Long userId, Long petId) {
        favoriteRepository.deleteByUserIdAndPetId(userId, petId);
    }

}
