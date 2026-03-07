package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.UserPetFavoriteDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.UserPetFavoriteService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPetFavoriteController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class UserPetFavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserPetFavoriteService favoriteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserPetFavoriteDTO buildDTO() {
        return UserPetFavoriteDTO.builder()
                .id(1L)
                .userId(1L)
                .petId(1L)
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/favorites/user/{userId} devuelve favoritos del usuario")
    @WithMockUser
    void shouldGetFavoritesByUser() throws Exception {
        when(favoriteService.findByUserId(1L)).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/favorites/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].petId").value(1));
    }

    @Test
    @DisplayName("GET /api/favorites/{id} devuelve favorito por ID")
    @WithMockUser
    void shouldGetFavoriteById() throws Exception {
        when(favoriteService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/favorites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("GET /api/favorites/check devuelve true si es favorito")
    @WithMockUser
    void shouldCheckIfIsFavorite() throws Exception {
        when(favoriteService.isFavorite(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/api/favorites/check")
                        .param("userId", "1")
                        .param("petId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("GET /api/favorites/check devuelve false si no es favorito")
    @WithMockUser
    void shouldCheckIfIsNotFavorite() throws Exception {
        when(favoriteService.isFavorite(1L, 2L)).thenReturn(false);

        mockMvc.perform(get("/api/favorites/check")
                        .param("userId", "1")
                        .param("petId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/favorites autenticado añade favorito y devuelve 201")
    @WithMockUser
    void shouldAddFavorite() throws Exception {
        UserPetFavoriteDTO dto = buildDTO();
        when(favoriteService.save(any(UserPetFavoriteDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.petId").value(1));
    }

    @Test
    @DisplayName("POST /api/favorites sin autenticación devuelve 403")
    void shouldReturn403WhenAddFavoriteUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/favorites/{id} autenticado elimina favorito y devuelve 204")
    @WithMockUser
    void shouldDeleteFavorite() throws Exception {
        doNothing().when(favoriteService).deleteById(1L);

        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/favorites/{id} sin autenticación devuelve 403")
    void shouldReturn403WhenDeleteFavoriteUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/favorites/user/{userId}/pet/{petId} autenticado elimina favorito y devuelve 204")
    @WithMockUser
    void shouldRemoveFavoriteByUserAndPet() throws Exception {
        doNothing().when(favoriteService).removeFavorite(1L, 1L);

        mockMvc.perform(delete("/api/favorites/user/1/pet/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/favorites/user/{userId}/pet/{petId} sin autenticación devuelve 403")
    void shouldReturn403WhenRemoveFavoriteUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/favorites/user/1/pet/1"))
                .andExpect(status().isForbidden());
    }
}