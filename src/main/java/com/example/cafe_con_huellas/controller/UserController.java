package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.dto.UserSummaryDTO;
import com.example.cafe_con_huellas.mapper.UserMapper;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios del sistema.
 * <p>
 * Diferencia entre perfiles detallados y resúmenes para listados.
 * Todos los endpoints requieren rol ADMIN. El registro público
 * de usuarios se gestiona a través de {@code /api/auth/register}.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Obtiene una lista simplificada de todos los usuarios del sistema.
     * Requiere rol ADMIN.
     *
     * @return lista de {@link UserSummaryDTO} con el resumen de cada usuario
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserSummaryDTO> getAllUsers() {
        return userService.findAll().stream()
                .map((UserDetailDTO dto) -> userMapper.toSummaryDto(dto))
                .toList();
    }

    /**
     * Obtiene el perfil completo de un usuario por su identificador.
     * Requiere rol ADMIN.
     *
     * @param id identificador único del usuario
     * @return {@link UserDetailDTO} con todos los datos del usuario
     * @throws ResourceNotFoundException si no existe usuario con ese ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailDTO getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    /**
     * Crea un nuevo usuario desde el panel de administración.
     * <p>
     * Recibe un {@link RegisterDTO} con la contraseña y devuelve
     * un {@link UserDetailDTO} sin ella. Para el registro público
     * usar {@code /api/auth/register}.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param registerDTO datos del nuevo usuario a registrar
     * @return {@link UserDetailDTO} con la información del usuario creado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailDTO registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * Actualiza la información personal de un usuario existente.
     * <p>
     * Utiliza un método controlado en el servicio para no sobrescribir la contraseña.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param id            identificador del usuario a actualizar
     * @param userDetailDTO nuevos datos del perfil del usuario
     * @return {@link UserDetailDTO} con los datos actualizados
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailDTO updateProfile(@PathVariable Long id, @Valid @RequestBody UserDetailDTO userDetailDTO) {
        return userService.updateProfile(id, userDetailDTO);
    }

    /**
     * Elimina un usuario del sistema de forma permanente.
     * Requiere rol ADMIN.
     *
     * @param id identificador del usuario a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

}