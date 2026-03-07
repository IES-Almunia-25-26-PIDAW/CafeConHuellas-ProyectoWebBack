package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AuthResponseDTO;
import com.example.cafe_con_huellas.dto.LoginDTO;
import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.UserRepository;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Login exitoso devuelve token y datos del usuario")
    void shouldLoginSuccessfully() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("ana@test.com")
                .password("password123")
                .build();

        User user = User.builder()
                .email("ana@test.com")
                .role(Role.USER)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("ana@test.com", "password123"));
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("ana@test.com")).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.email").value("ana@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("Login con credenciales incorrectas devuelve 401")
    void shouldReturn401WhenBadCredentials() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("ana@test.com")
                .password("wrongpassword")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("Login con email inválido devuelve 400")
    void shouldReturn400WhenEmailInvalid() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("no-es-un-email")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Registro exitoso devuelve 201 y datos del usuario")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("password123")
                .phone("612345678")
                .build();

        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                .email("ana@test.com")
                .firstName("Ana")
                .lastName1("Cruces")
                .role("USER")
                .build();

        when(userService.register(any(RegisterDTO.class))).thenReturn(userDetailDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@test.com"))
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @DisplayName("Registro con datos inválidos devuelve 400")
    void shouldReturn400WhenRegisterDataInvalid() throws Exception {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstName("")
                .lastName1("")
                .email("no-es-email")
                .password("123")
                .phone("abc")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }
}