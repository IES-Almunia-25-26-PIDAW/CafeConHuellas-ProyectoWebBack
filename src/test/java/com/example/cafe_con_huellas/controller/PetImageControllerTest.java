package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.PetImageService;
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

@WebMvcTest(PetImageController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class PetImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetImageService petImageService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private PetImageDTO buildDTO() {
        return new PetImageDTO(1L, 1L, "https://example.com/firu.jpg");
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/pet-images devuelve lista de imágenes")
    @WithMockUser
    void shouldGetAllImages() throws Exception {
        when(petImageService.findAll()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/pet-images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/firu.jpg"));
    }

    @Test
    @DisplayName("GET /api/pet-images/{id} devuelve imagen por ID")
    @WithMockUser
    void shouldGetImageById() throws Exception {
        when(petImageService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/pet-images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petId").value(1));
    }

    @Test
    @DisplayName("GET /api/pet-images/pet/{petId} devuelve imágenes de una mascota")
    @WithMockUser
    void shouldGetImagesByPet() throws Exception {
        when(petImageService.findByPetId(1L)).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/pet-images/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/firu.jpg"));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/pet-images con ADMIN añade imagen y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldAddImageAsAdmin() throws Exception {
        PetImageDTO dto = buildDTO();
        when(petImageService.save(any(PetImageDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/pet-images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/firu.jpg"));
    }

    @Test
    @DisplayName("POST /api/pet-images sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenAddImageAsUser() throws Exception {
        mockMvc.perform(post("/api/pet-images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/pet-images/{id} con ADMIN elimina imagen y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteImageAsAdmin() throws Exception {
        doNothing().when(petImageService).deleteById(1L);

        mockMvc.perform(delete("/api/pet-images/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/pet-images/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteImageAsUser() throws Exception {
        mockMvc.perform(delete("/api/pet-images/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/pet-images/pet/{petId} con ADMIN elimina todas las imágenes y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteByPetIdAsAdmin() throws Exception {
        doNothing().when(petImageService).deleteByPetId(1L);

        mockMvc.perform(delete("/api/pet-images/pet/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/pet-images/pet/{petId} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteByPetIdAsUser() throws Exception {
        mockMvc.perform(delete("/api/pet-images/pet/1"))
                .andExpect(status().isForbidden());
    }
}