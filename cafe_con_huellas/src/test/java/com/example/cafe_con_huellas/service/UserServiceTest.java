package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserMapper;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Tests unitarios de UserService usando Mockito
// No carga el contexto de Spring, es más rápido
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Simulamos las dependencias con Mockito para no tocar la BD real
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Inyectamos los mocks en el servicio que vamos a testear
    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDetailDTO testUserDetailDTO;
    private RegisterDTO testRegisterDTO;

    // Inicializamos los datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Ana");
        testUser.setLastName1("Cruces");
        testUser.setEmail("ana@test.com");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setPhone("612345678");
        testUser.setRole(Role.USER);

        testUserDetailDTO = new UserDetailDTO();
        testUserDetailDTO.setId(1L);
        testUserDetailDTO.setFirstName("Ana");
        testUserDetailDTO.setLastName1("Cruces");
        testUserDetailDTO.setEmail("ana@test.com");

        testRegisterDTO = RegisterDTO.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("12345678")
                .phone("612345678")
                .build();
    }

    @Test
    @DisplayName("Debe devolver todos los usuarios correctamente")
    void shouldFindAllUsers() {
        // Simulamos que el repositorio devuelve una lista con un usuario
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userMapper.toDetailDto(testUser)).thenReturn(testUserDetailDTO);

        List<UserDetailDTO> result = userService.findAll();

        // Verificamos que devuelve 1 usuario
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("ana@test.com");
    }

    @Test
    @DisplayName("Debe encontrar un usuario por su ID")
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDetailDto(testUser)).thenReturn(testUserDetailDTO);

        UserDetailDTO result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("ana@test.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Simulamos que no se encuentra el usuario
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Verificamos que lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    @DisplayName("Debe registrar un usuario nuevo correctamente")
    void shouldRegisterNewUser() {
        // Simulamos que el email no existe todavía
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // Simulamos la encriptación de la contraseña
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
        // Simulamos el guardado en BD
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDetailDto(testUser)).thenReturn(testUserDetailDTO);

        UserDetailDTO result = userService.register(testRegisterDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("ana@test.com");
        // Verificamos que se llamó al encoder exactamente una vez
        verify(passwordEncoder, times(1)).encode("12345678");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya está registrado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Simulamos que el email ya existe en la BD
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);

        // Debe lanzar BadRequestException
        assertThrows(BadRequestException.class, () -> userService.register(testRegisterDTO));

        // Verificamos que nunca se intentó guardar en BD
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar un usuario existente correctamente")
    void shouldDeleteUserById() {
        // Simulamos existsById en lugar de findById porque así lo usa tu servicio
        when(userRepository.existsById(1L)).thenReturn(true);

        // No lanza excepción
        assertDoesNotThrow(() -> userService.deleteById(1L));

        // Verificamos que se llamó al delete exactamente una vez
        verify(userRepository, times(1)).deleteById(1L);
    }
}