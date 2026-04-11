package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.VaccineService;
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

@WebMvcTest(VaccineController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class VaccineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VaccineService vaccineService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private VaccineDTO buildDTO() {
        return VaccineDTO.builder()
                .id(1L)
                .name("Rabia")
                .description("Vacuna obligatoria contra la rabia para perros y gatos")
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/vaccines devuelve catálogo de vacunas")
    @WithMockUser
    void shouldGetAllVaccines() throws Exception {
        when(vaccineService.findAll()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/vaccines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rabia"));
    }

    @Test
    @DisplayName("GET /api/vaccines sin autenticación devuelve 403")
    void shouldReturn403WhenGetAllUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/vaccines"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/vaccines/{id} devuelve vacuna por ID")
    @WithMockUser
    void shouldGetVaccineById() throws Exception {
        when(vaccineService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/vaccines/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rabia"))
                .andExpect(jsonPath("$.description").value("Vacuna obligatoria contra la rabia para perros y gatos"));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/vaccines con ADMIN crea vacuna y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateVaccineAsAdmin() throws Exception {
        VaccineDTO dto = buildDTO();
        when(vaccineService.save(any(VaccineDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/vaccines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rabia"));
    }

    @Test
    @DisplayName("POST /api/vaccines sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenCreateAsUser() throws Exception {
        mockMvc.perform(post("/api/vaccines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/vaccines/{id} con ADMIN elimina vacuna y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteVaccineAsAdmin() throws Exception {
        doNothing().when(vaccineService).deleteById(1L);

        mockMvc.perform(delete("/api/vaccines/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/vaccines/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteAsUser() throws Exception {
        mockMvc.perform(delete("/api/vaccines/1"))
                .andExpect(status().isForbidden());
    }
}