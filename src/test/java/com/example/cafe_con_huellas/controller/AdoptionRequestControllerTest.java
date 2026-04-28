package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.AdoptionRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdoptionRequestController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AdoptionRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdoptionRequestService requestService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private AdoptionRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = AdoptionRequestDTO.builder()
                .id(1L)
                .userName("María García")
                .userEmail("maria@example.com")
                .petName("Firu")
                .address("Calle Mayor 1")
                .city("Jerez")
                .housingType("PISO")
                .hasGarden(false)
                .hasOtherPets(false)
                .hasChildren(false)
                .hoursAlonePerDay(4)
                .experienceWithPets(true)
                .reasonForAdoption("Quiero darle un hogar a un perro que lo necesite")
                .agreesToFollowUp(true)
                .status(AdoptionRequestStatus.PENDIENTE)
                .build();
    }

    // -------------------- GET /api/adoption-requests --------------------

    @Test
    @DisplayName("GET /api/adoption-requests con ADMIN devuelve lista de solicitudes")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllRequestsAsAdmin() throws Exception {
        when(requestService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/adoption-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userName").value("María García"))
                .andExpect(jsonPath("$[0].petName").value("Firu"));
    }

    @Test
    @DisplayName("GET /api/adoption-requests sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetAllAsUser() throws Exception {
        mockMvc.perform(get("/api/adoption-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/adoption-requests?email=xxx con ADMIN devuelve solicitudes de ese usuario")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnRequestsByEmailAsAdmin() throws Exception {
        when(requestService.findByUserEmail("maria@example.com")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/adoption-requests")
                        .param("email", "maria@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userEmail").value("maria@example.com"));
    }

    @Test
    @DisplayName("GET /api/adoption-requests?email=xxx sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenFilterByEmailAsUser() throws Exception {
        mockMvc.perform(get("/api/adoption-requests")
                        .param("email", "maria@example.com"))
                .andExpect(status().isForbidden());
    }

    // -------------------- GET /api/adoption-requests/me --------------------

    @Test
    @DisplayName("GET /api/adoption-requests/me con usuario autenticado devuelve sus solicitudes")
    @WithMockUser(username = "maria@example.com", roles = "USER")
    void shouldGetMyRequestsAsUser() throws Exception {
        when(requestService.findByUserEmail("maria@example.com")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/adoption-requests/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userEmail").value("maria@example.com"));
    }

    @Test
    @DisplayName("GET /api/adoption-requests/me sin autenticación devuelve 403")
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/adoption-requests/me"))
                .andExpect(status().isForbidden());
    }

    // -------------------- GET /api/adoption-requests/status/{status} --------------------

    @Test
    @DisplayName("GET /api/adoption-requests/status/PENDIENTE con ADMIN devuelve solicitudes pendientes")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnRequestsByStatus() throws Exception {
        when(requestService.findByStatus(AdoptionRequestStatus.PENDIENTE)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/adoption-requests/status/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDIENTE"));
    }

    @Test
    @DisplayName("GET /api/adoption-requests/status/PENDIENTE sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByStatusAsUser() throws Exception {
        mockMvc.perform(get("/api/adoption-requests/status/PENDIENTE"))
                .andExpect(status().isForbidden());
    }

    // -------------------- GET /api/adoption-requests/{id} --------------------

    @Test
    @DisplayName("GET /api/adoption-requests/{id} con ADMIN devuelve la solicitud")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnRequestById() throws Exception {
        when(requestService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/adoption-requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.city").value("Jerez"));
    }

    @Test
    @DisplayName("GET /api/adoption-requests/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/adoption-requests/1"))
                .andExpect(status().isForbidden());
    }

    // -------------------- GET /api/adoption-requests/relationship/{relationshipId} --------------------

    @Test
    @DisplayName("GET /api/adoption-requests/relationship/{relationshipId} con ADMIN devuelve la solicitud")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnRequestByRelationshipId() throws Exception {
        when(requestService.findByRelationshipId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/adoption-requests/relationship/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("María García"));
    }

    @Test
    @DisplayName("GET /api/adoption-requests/relationship/{relationshipId} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByRelationshipIdAsUser() throws Exception {
        mockMvc.perform(get("/api/adoption-requests/relationship/1"))
                .andExpect(status().isForbidden());
    }


    // -------------------- PATCH /api/adoption-requests/{id}/status --------------------

    @Test
    @DisplayName("PATCH /api/adoption-requests/{id}/status con ADMIN aprueba la solicitud")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateStatusAsAdmin() throws Exception {
        AdoptionRequestDTO approvedDTO = AdoptionRequestDTO.builder()
                .id(1L)
                .status(AdoptionRequestStatus.APROBADA)
                .build();

        when(requestService.updateStatus(1L, AdoptionRequestStatus.APROBADA)).thenReturn(approvedDTO);

        mockMvc.perform(patch("/api/adoption-requests/1/status")
                        .param("status", "APROBADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROBADA"));
    }

    @Test
    @DisplayName("PATCH /api/adoption-requests/{id}/status sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUpdateStatusAsUser() throws Exception {
        mockMvc.perform(patch("/api/adoption-requests/1/status")
                        .param("status", "APROBADA"))
                .andExpect(status().isForbidden());
    }
}