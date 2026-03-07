package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.mapper.UserMapper;
import com.example.cafe_con_huellas.mapper.UserMapperImpl;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, UserMapperImpl.class})
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserDetailDTO buildUserDetailDTO() {
        return UserDetailDTO.builder()
                .id(1L)
                .firstName("María")
                .lastName1("García")
                .lastName2("López")
                .email("maria@example.com")
                .phone("612345678")
                .role("USER")
                .imageUrl("https://example.com/maria.jpg")
                .build();
    }

    private RegisterDTO buildRegisterDTO() {
        return RegisterDTO.builder()
                .firstName("María")
                .lastName1("García")
                .lastName2("López")
                .email("maria@example.com")
                .password("password123")
                .phone("612345678")
                .role("USER")
                .imageUrl("https://example.com/maria.jpg")
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/users con ADMIN devuelve lista de usuarios")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersAsAdmin() throws Exception {
        when(userService.findAll()).thenAnswer(inv -> List.of(buildUserDetailDTO()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("maria@example.com"));
    }

    @Test
    @DisplayName("GET /api/users sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetAllAsUser() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/users/{id} con ADMIN devuelve usuario")
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByIdAsAdmin() throws Exception {
        when(userService.findById(1L)).thenReturn(buildUserDetailDTO());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María"));
    }

    @Test
    @DisplayName("GET /api/users/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/users con ADMIN crea usuario y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserAsAdmin() throws Exception {
        when(userService.register(any(RegisterDTO.class))).thenReturn(buildUserDetailDTO());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRegisterDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria@example.com"));
    }

    @Test
    @DisplayName("POST /api/users sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenCreateAsUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRegisterDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- PUT --------------------

    @Test
    @DisplayName("PUT /api/users/{id} con ADMIN actualiza usuario")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUserAsAdmin() throws Exception {
        UserDetailDTO dto = buildUserDetailDTO();
        when(userService.updateProfile(eq(1L), any(UserDetailDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUpdateAsUser() throws Exception {
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUserDetailDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/users/{id} con ADMIN elimina usuario y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserAsAdmin() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteAsUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());
    }
}