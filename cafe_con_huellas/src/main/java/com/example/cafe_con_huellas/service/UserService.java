package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserMapper;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Servicio encargado de la lógica de negocio relacionada con los usuarios
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // ---------- CRUD BÁSICO ----------

    // Obtiene todos los usuarios en formato detalle
    @Transactional(readOnly = true)
    public List<UserDetailDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    // Busca un usuario por ID y devuelve su ficha completa
    @Transactional(readOnly = true)
    public UserDetailDTO findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDetailDto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    // Registra un nuevo usuario validando que el email no exista
    @Transactional
    public UserDetailDTO register(UserDetailDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("El email " + dto.getEmail() + " ya está registrado.");
        }

        User user = userMapper.toEntity(dto);
        // Nota: El password debería gestionarse en un DTO de Registro aparte o
        // asegurar que el mapper lo trate si viene en el JSON inicial.

        return userMapper.toDetailDto(userRepository.save(user));
    }

    // Actualiza los datos del perfil desde el DTO de detalle
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

    // Elimina un usuario por su ID
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Busca un usuario por su email único
    @Transactional(readOnly = true)
    public UserDetailDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDetailDto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    // Devuelve usuarios filtrados por su rol (ADMIN, USER)
    @Transactional(readOnly = true)
    public List<UserDetailDTO> findByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    // ---------- SEGURIDAD Y ESTADÍSTICAS ----------

    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.count();
    }


}
