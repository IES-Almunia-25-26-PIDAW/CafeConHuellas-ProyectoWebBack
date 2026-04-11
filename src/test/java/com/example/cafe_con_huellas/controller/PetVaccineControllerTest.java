package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.PetVaccineService;
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

@WebMvcTest(PetVaccineController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class PetVaccineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetVaccineService petVaccineService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private PetVaccineDTO buildDTO() {
        return PetVaccineDTO.builder()
                .id(1L)
                .petId(1L)
                .vaccineId(1L)
                .dateAdministered(LocalDate.now().minusMonths(1))
                .nextDoseDate(LocalDate.now().plusMonths(11))
                .notes("Sin reacciones adversas")
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/pet-vaccines/pet/{petId} devuelve historial de vacunas")
    @WithMockUser
    void shouldGetVaccinesByPet() throws Exception {
        when(petVaccineService.findByPetId(1L)).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/pet-vaccines/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petId").value(1))
                .andExpect(jsonPath("$[0].notes").value("Sin reacciones adversas"));
    }

    @Test
    @DisplayName("GET /api/pet-vaccines/{id} devuelve registro de vacunación por ID")
    @WithMockUser
    void shouldGetVaccineById() throws Exception {
        when(petVaccineService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/pet-vaccines/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vaccineId").value(1));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/pet-vaccines con ADMIN registra vacuna y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldAddVaccineAsAdmin() throws Exception {
        PetVaccineDTO dto = buildDTO();
        when(petVaccineService.save(any(PetVaccineDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/pet-vaccines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.petId").value(1));
    }

    @Test
    @DisplayName("POST /api/pet-vaccines sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenAddVaccineAsUser() throws Exception {
        mockMvc.perform(post("/api/pet-vaccines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- PUT --------------------

    @Test
    @DisplayName("PUT /api/pet-vaccines/{id} con ADMIN actualiza registro")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateVaccineRecordAsAdmin() throws Exception {
        PetVaccineDTO dto = buildDTO();
        when(petVaccineService.updateMedicalInfo(eq(1L), any(PetVaccineDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/pet-vaccines/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Sin reacciones adversas"));
    }

    @Test
    @DisplayName("PUT /api/pet-vaccines/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUpdateAsUser() throws Exception {
        mockMvc.perform(put("/api/pet-vaccines/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/pet-vaccines/{id} con ADMIN elimina registro y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteVaccineRecordAsAdmin() throws Exception {
        doNothing().when(petVaccineService).deleteById(1L);

        mockMvc.perform(delete("/api/pet-vaccines/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/pet-vaccines/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteAsUser() throws Exception {
        mockMvc.perform(delete("/api/pet-vaccines/1"))
                .andExpect(status().isForbidden());
    }
}