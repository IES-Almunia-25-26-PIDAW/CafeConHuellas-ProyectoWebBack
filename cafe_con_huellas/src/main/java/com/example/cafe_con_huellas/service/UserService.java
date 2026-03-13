package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserMapper;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la lógica de negocio relacionada con los usuarios del sistema.
 * <p>
 * Gestiona el registro, consulta, actualización y eliminación de usuarios,
 * encriptando las contraseñas con BCrypt y validando la unicidad del email.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Encriptador de contraseñas
    private final PasswordEncoder passwordEncoder;


    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todos los usuarios del sistema en formato detallado.
     *
     * @return lista de {@link UserDetailDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<UserDetailDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador único del usuario
     * @return {@link UserDetailDTO} con la ficha completa del usuario
     * @throws ResourceNotFoundException si no existe el usuario con ese ID
     */
    @Transactional(readOnly = true)
    public UserDetailDTO findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDetailDto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }


    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Valida que el email no esté ya registrado, encripta la contraseña con BCrypt
     * y asigna el rol {@code USER} por defecto si no se especifica otro.
     * </p>
     *
     * @param dto datos del nuevo usuario, incluyendo la contraseña en texto plano
     * @return {@link UserDetailDTO} con el usuario creado, sin exponer la contraseña
     * @throws BadRequestException si el email ya está registrado
     */
    @Transactional
    public UserDetailDTO register(RegisterDTO dto) {

        // Bloqueamos el registro si el email ya existe en la BD
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("El email " + dto.getEmail() + " ya está registrado.");
        }

        // Construimos la entidad manualmente para controlar qué campos se asignan
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName1(dto.getLastName1());
        user.setLastName2(dto.getLastName2());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setImageUrl(dto.getImageUrl());

        // BCrypt transforma "micontraseña" en algo como "$2a$10$xyz..."
        // Es irreversible: nunca se puede recuperar la contraseña original
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Asignamos rol desde el DTO o USER por defecto si no viene
        if (dto.getRole() != null) {
            user.setRole(userMapper.roleFromString(dto.getRole()));
        } else {
            user.setRole(Role.USER);
        }

        // Guardamos y devolvemos el perfil sin exponer la contraseña
        return userMapper.toDetailDto(userRepository.save(user));
    }

    /**
     * Actualiza los datos del perfil de un usuario existente.
     * <p>
     * No modifica la contraseña. Solo actualiza nombre, apellidos,
     * teléfono, imagen y rol si se proporcionan.
     * </p>
     *
     * @param id  identificador del usuario a actualizar
     * @param dto nuevos datos del perfil
     * @return {@link UserDetailDTO} con los datos actualizados
     * @throws ResourceNotFoundException si no existe el usuario con ese ID
     */
    @Transactional
    public UserDetailDTO updateProfile(Long id, UserDetailDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Usuario no encontrado"));

        user.setFirstName(dto.getFirstName());
        user.setLastName1(dto.getLastName1());
        user.setLastName2(dto.getLastName2());
        user.setPhone(dto.getPhone());
        user.setImageUrl(dto.getImageUrl());

        // Actualizamos el rol si viene en el DTO (usando el método de conversión del mapper)
        if (dto.getRole() != null) {
            user.setRole(userMapper.roleFromString(dto.getRole()));
        }

        return userMapper.toDetailDto(userRepository.save(user));
    }

    /**
     * Elimina un usuario del sistema de forma permanente.
     *
     * @param id identificador del usuario a eliminar
     * @throws ResourceNotFoundException si no existe el usuario con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    /**
     * Busca un usuario por su dirección de email.
     *
     * @param email email único del usuario
     * @return {@link UserDetailDTO} con los datos del usuario
     * @throws ResourceNotFoundException si no existe ningún usuario con ese email
     */
    @Transactional(readOnly = true)
    public UserDetailDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDetailDto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Obtiene todos los usuarios que tienen un rol específico.
     *
     * @param role rol por el que filtrar ({@link Role})
     * @return lista de {@link UserDetailDTO} con el rol indicado
     */
    @Transactional(readOnly = true)
    public List<UserDetailDTO> findByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    // ---------- SEGURIDAD Y ESTADÍSTICAS ----------

    /**
     * Actualiza la contraseña de un usuario.
     * <p>
     * La contraseña debe llegar ya encriptada desde el servicio que invoca este método.
     * </p>
     *
     * @param id          identificador del usuario
     * @param newPassword nueva contraseña encriptada
     * @throws ResourceNotFoundException si no existe el usuario con ese ID
     */
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    /**
     * Devuelve el número total de usuarios registrados en el sistema.
     *
     * @return total de usuarios
     */
    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.count();
    }


}
