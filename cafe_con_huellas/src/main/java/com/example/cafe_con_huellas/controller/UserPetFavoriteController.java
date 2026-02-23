package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.UserPetFavoriteDTO;
import com.example.cafe_con_huellas.mapper.UserPetFavoriteMapper;
import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import com.example.cafe_con_huellas.service.UserPetFavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* Controlador para gestionar la lista de mascotas favoritas.
 * Permite a los usuarios guardar las mascotas en las que están interesados.
 */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class UserPetFavoriteController {

    private final UserPetFavoriteService favoriteService;
    private final UserPetFavoriteMapper favoriteMapper;

    // Obtiene todas las mascotas favoritas de un usuario concreto
    @GetMapping("/user/{userId}")
    public List<UserPetFavoriteDTO> getFavoritesByUser(@PathVariable Long userId) {
        return favoriteService.findByUserId(userId).stream()
                .map(favoriteMapper::toDto)
                .toList();
    }

    // Añade una mascota a la lista de favoritos del usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPetFavoriteDTO addFavorite(@Valid @RequestBody UserPetFavoriteDTO dto) {
        UserPetFavorite entity = favoriteMapper.toEntity(dto);
        return favoriteMapper.toDto(favoriteService.save(entity));
    }


    // Elimina una mascota de la lista de favoritos
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@PathVariable Long id) {
        favoriteService.deleteById(id);
    }
}