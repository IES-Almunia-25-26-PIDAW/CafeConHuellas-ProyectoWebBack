package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.AdoptionDetailService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdoptionDetailController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AdoptionDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdoptionDetailService adoptionDetailService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private AdoptionDetailDTO buildDTO() {
        return AdoptionDetailDTO.builder()
                .id(1L)
                .userPetRelationshipId(1L)
                .adoptionDate(LocalDate.now().minusMonths(1))
                .place("Refugio Jerez")
                .conditions("Sin condiciones especiales")
                .notes("Todo correcto")
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/adoption-details con ADMIN devuelve lista")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllDetailsAsAdmin() throws Exception {
        when(adoptionDetailService.findAll()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/adoption-details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].place").value("Refugio Jerez"));
    }

    @Test
    @DisplayName("GET /api/adoption-details sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetAllAsUser() throws Exception {
        mockMvc.perform(get("/api/adoption-details"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/adoption-details/{id} con ADMIN devuelve detalle")
    @WithMockUser(roles = "ADMIN")
    void shouldGetDetailByIdAsAdmin() throws Exception {
        when(adoptionDetailService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/adoption-details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.place").value("Refugio Jerez"));
    }

    @Test
    @DisplayName("GET /api/adoption-details/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/adoption-details/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/adoption-details/relationship/{id} con ADMIN devuelve detalle")
    @WithMockUser(roles = "ADMIN")
    void shouldGetByRelationshipIdAsAdmin() throws Exception {
        when(adoptionDetailService.findByRelationshipId(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/adoption-details/relationship/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userPetRelationshipId").value(1));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/adoption-details con ADMIN crea detalle y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateDetailAsAdmin() throws Exception {
        AdoptionDetailDTO dto = buildDTO();
        when(adoptionDetailService.save(any(AdoptionDetailDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/adoption-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.place").value("Refugio Jerez"));
    }

    @Test
    @DisplayName("POST /api/adoption-details sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenCreateAsUser() throws Exception {
        mockMvc.perform(post("/api/adoption-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- PUT --------------------

    @Test
    @DisplayName("PUT /api/adoption-details/{id} con ADMIN actualiza detalle")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateDetailAsAdmin() throws Exception {
        AdoptionDetailDTO dto = buildDTO();
        when(adoptionDetailService.updateDetails(eq(1L), any(AdoptionDetailDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/adoption-details/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.place").value("Refugio Jerez"));
    }

    @Test
    @DisplayName("PUT /api/adoption-details/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUpdateAsUser() throws Exception {
        mockMvc.perform(put("/api/adoption-details/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/adoption-details/{id} con ADMIN elimina y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteDetailAsAdmin() throws Exception {
        doNothing().when(adoptionDetailService).deleteById(1L);

        mockMvc.perform(delete("/api/adoption-details/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/adoption-details/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteAsUser() throws Exception {
        mockMvc.perform(delete("/api/adoption-details/1"))
                .andExpect(status().isForbidden());
    }
}