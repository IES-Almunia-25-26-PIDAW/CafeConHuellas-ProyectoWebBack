package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.UserPetRelationshipService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPetRelationshipController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class UserPetRelationshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserPetRelationshipService relationshipService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserPetRelationshipDTO buildDTO() {
        return UserPetRelationshipDTO.builder()
                .id(1L)
                .userId(1L)
                .petId(1L)
                .relationshipType("ADOPCION")
                .startDate(LocalDate.now().minusDays(10))
                .active(true)
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/relationships con ADMIN devuelve lista")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllRelationshipsAsAdmin() throws Exception {
        when(relationshipService.findAll()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/relationships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].relationshipType").value("ADOPCION"));
    }

    @Test
    @DisplayName("GET /api/relationships sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetAllAsUser() throws Exception {
        mockMvc.perform(get("/api/relationships"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/relationships/{id} con ADMIN devuelve relación")
    @WithMockUser(roles = "ADMIN")
    void shouldGetRelationshipByIdAsAdmin() throws Exception {
        when(relationshipService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/relationships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("GET /api/relationships/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/relationships/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/relationships/active con ADMIN devuelve relaciones activas")
    @WithMockUser(roles = "ADMIN")
    void shouldGetActiveRelationshipsAsAdmin() throws Exception {
        when(relationshipService.findActiveRelationships()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/relationships/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    @DisplayName("GET /api/relationships/active sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetActiveAsUser() throws Exception {
        mockMvc.perform(get("/api/relationships/active"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/relationships/user/{userId} devuelve relaciones del usuario")
    @WithMockUser
    void shouldGetRelationshipsByUser() throws Exception {
        when(relationshipService.findByUserId(1L)).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/relationships/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("GET /api/relationships/pet/{petId} devuelve relaciones de la mascota")
    @WithMockUser
    void shouldGetRelationshipsByPet() throws Exception {
        when(relationshipService.findByPetId(1L)).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/relationships/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petId").value(1));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/relationships con ADMIN crea relación y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateRelationshipAsAdmin() throws Exception {
        UserPetRelationshipDTO dto = buildDTO();
        when(relationshipService.save(any(UserPetRelationshipDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.relationshipType").value("ADOPCION"));
    }

    @Test
    @DisplayName("POST /api/relationships sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenCreateAsUser() throws Exception {
        mockMvc.perform(post("/api/relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- POST /me --------------------

    @Test
    @DisplayName("POST /api/relationships/me con USER crea relación y devuelve 201")
    @WithMockUser(username = "maria@example.com", roles = "USER")
    void shouldCreateRelationshipAsUser() throws Exception {
        UserPetRelationshipDTO dto = buildDTO(); // usa el builder que ya tenéis en el test
        dto.setRelationshipType("ACOGIDA");

        when(relationshipService.saveAsUser(eq("maria@example.com"), any(UserPetRelationshipDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/relationships/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.relationshipType").value("ACOGIDA"));
    }

    @Test
    @DisplayName("POST /api/relationships/me sin autenticación devuelve 403")
    void shouldReturn403WhenCreateRelationshipAsUserUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/relationships/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserPetRelationshipDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- PUT --------------------

    @Test
    @DisplayName("PUT /api/relationships/{id} con ADMIN actualiza relación y devuelve 200")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateRelationshipAsAdmin() throws Exception {
        UserPetRelationshipDTO dto = buildDTO();
        when(relationshipService.update(eq(1L), any(UserPetRelationshipDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/relationships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.relationshipType").value("ADOPCION"));
    }

    @Test
    @DisplayName("PUT /api/relationships/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUpdateAsUser() throws Exception {
        mockMvc.perform(put("/api/relationships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- PATCH --------------------

    @Test
    @DisplayName("PATCH /api/relationships/{id}/end con ADMIN finaliza relación y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldEndRelationshipAsAdmin() throws Exception {
        doNothing().when(relationshipService).endRelationship(eq(1L), any());

        mockMvc.perform(patch("/api/relationships/1/end"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/relationships/{id}/end sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenEndRelationshipAsUser() throws Exception {
        mockMvc.perform(patch("/api/relationships/1/end"))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/relationships/{id} con ADMIN elimina relación y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteRelationshipAsAdmin() throws Exception {
        doNothing().when(relationshipService).deleteById(1L);

        mockMvc.perform(delete("/api/relationships/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/relationships/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteAsUser() throws Exception {
        mockMvc.perform(delete("/api/relationships/1"))
                .andExpect(status().isForbidden());
    }
}