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

    // Obtiene todas las mascotas favoritas de un usuario concreto
    @GetMapping("/user/{userId}")
    public List<UserPetFavoriteDTO> getFavoritesByUser(@PathVariable Long userId) {
        // El servicio ya devuelve la lista de DTOs mapeada
        return favoriteService.findByUserId(userId);
    }

    // Busca un registro de favorito específico por su ID
    @GetMapping("/{id}")
    public UserPetFavoriteDTO getFavoriteById(@PathVariable Long id) {
        return favoriteService.findById(id);
    }

    // Comprueba si una mascota específica es favorita para un usuario
    @GetMapping("/check")
    public boolean checkIfIsFavorite(@RequestParam Long userId, @RequestParam Long petId) {
        return favoriteService.isFavorite(userId, petId);
    }

    /* * Añade una mascota a la lista de favoritos del usuario.
     * El servicio valida que no existan duplicados y que el usuario/mascota existan.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPetFavoriteDTO addFavorite(@Valid @RequestBody UserPetFavoriteDTO dto) {
        return favoriteService.save(dto);
    }

    // Elimina un registro de favorito mediante su ID único
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteById(id);
    }

    /* * Elimina una mascota de favoritos usando los IDs de usuario y mascota.
     * Útil para el botón de favorito en la interfaz del catálogo.
     */
    @DeleteMapping("/user/{userId}/pet/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteByUserAndPet(@PathVariable Long userId, @PathVariable Long petId) {
        favoriteService.removeFavorite(userId, petId);
    }
}