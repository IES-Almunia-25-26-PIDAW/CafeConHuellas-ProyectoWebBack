package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.dto.UserSummaryDTO;
import com.example.cafe_con_huellas.mapper.UserMapper;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* API para la gestión de usuarios del sistema.
 * Diferencia entre perfiles detallados y resúmenes para listados.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    // Obtiene una lista simplificada de todos los usuarios (vista admin)
    @GetMapping
    public List<UserSummaryDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(userMapper::toSummaryDto)
                .toList();
    }

    // Obtiene el perfil completo de un usuario por su ID
    @GetMapping("/{id}")
    public UserDetailDTO getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    /* Actualiza la información personal del usuario.
     * Se utiliza un método controlado en el service para no sobrescribir la contraseña.
     */
    @PutMapping("/{id}")
    public UserDetailDTO updateProfile(@PathVariable Long id, @Valid @RequestBody UserDetailDTO userDetailDTO) {
        return userService.updateProfile(id, userDetailDTO);
    }

    // Elimina un usuario del sistema permanentemente.
    /* * @ResponseStatus(HttpStatus.NO_CONTENT) devuelve un código 204,
     * indicando que la acción se realizó con éxito pero no hay contenido que devolver.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

}