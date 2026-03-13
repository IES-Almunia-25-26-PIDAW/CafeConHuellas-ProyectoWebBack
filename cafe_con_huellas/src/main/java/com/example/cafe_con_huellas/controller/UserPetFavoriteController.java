package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.UserPetFavoriteDTO;
import com.example.cafe_con_huellas.mapper.UserPetFavoriteMapper;
import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.service.UserPetFavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las mascotas favoritas de los usuarios.
 * <p>
 * Permite a los usuarios marcar y desmarcar mascotas como favoritas,
 * consultar su lista y comprobar si una mascota concreta está en ella.
 * </p>
 */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class UserPetFavoriteController {

    private final UserPetFavoriteService favoriteService;

    /**
     * Obtiene todas las mascotas marcadas como favoritas por un usuario.
     *
     * @param userId identificador del usuario
     * @return lista de {@link UserPetFavoriteDTO} con sus favoritos
     */
    @GetMapping("/user/{userId}")
    public List<UserPetFavoriteDTO> getFavoritesByUser(@PathVariable Long userId) {
        // El servicio ya devuelve la lista de DTOs mapeada
        return favoriteService.findByUserId(userId);
    }

    /**
     * Obtiene un registro de favorito concreto por su identificador.
     *
     * @param id identificador único del registro de favorito
     * @return {@link UserPetFavoriteDTO} con los datos del registro
     * @throws ResourceNotFoundException si no existe el registro
     */
    @GetMapping("/{id}")
    public UserPetFavoriteDTO getFavoriteById(@PathVariable Long id) {
        return favoriteService.findById(id);
    }

    /**
     * Comprueba si una mascota específica está en la lista de favoritos de un usuario.
     *
     * @param userId identificador del usuario
     * @param petId  identificador de la mascota
     * @return {@code true} si la mascota es favorita del usuario, {@code false} en caso contrario
     */
    @GetMapping("/check")
    public boolean checkIfIsFavorite(@RequestParam Long userId, @RequestParam Long petId) {
        return favoriteService.isFavorite(userId, petId);
    }

    /**
     * Añade una mascota a la lista de favoritos de un usuario.
     * <p>
     * El servicio valida que no existan duplicados y que el usuario y la mascota existan.
     * </p>
     *
     * @param dto datos con el identificador del usuario y de la mascota
     * @return {@link UserPetFavoriteDTO} con el registro creado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPetFavoriteDTO addFavorite(@Valid @RequestBody UserPetFavoriteDTO dto) {
        return favoriteService.save(dto);
    }

    /**
     * Elimina un registro de favorito mediante su identificador único.
     *
     * @param id identificador del registro a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteById(id);
    }

    /**
     * Elimina una mascota de favoritos usando los identificadores de usuario y mascota.
     * <p>
     * Útil para el botón de favorito del catálogo sin necesidad de conocer el ID del registro.
     * </p>
     *
     * @param userId identificador del usuario
     * @param petId  identificador de la mascota a eliminar de favoritos
     */
    @DeleteMapping("/user/{userId}/pet/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteByUserAndPet(@PathVariable Long userId, @PathVariable Long petId) {
        favoriteService.removeFavorite(userId, petId);
    }
}