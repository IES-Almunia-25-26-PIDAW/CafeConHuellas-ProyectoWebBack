package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.DonationDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.DonationService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonationController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DonationService donationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private DonationDTO buildDTO() {
        return DonationDTO.builder()
                .id(1L)
                .userId(1L)
                .date(LocalDateTime.now().minusDays(1))
                .category("MONETARIA")
                .method("BIZUM")
                .amount(BigDecimal.valueOf(50.00))
                .notes("Donación de prueba")
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/donations con ADMIN devuelve lista")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllDonationsAsAdmin() throws Exception {
        when(donationService.findAll()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/donations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("MONETARIA"));
    }

    @Test
    @DisplayName("GET /api/donations sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetAllAsUser() throws Exception {
        mockMvc.perform(get("/api/donations"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/donations/{id} con ADMIN devuelve donación")
    @WithMockUser(roles = "ADMIN")
    void shouldGetDonationByIdAsAdmin() throws Exception {
        when(donationService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/donations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("BIZUM"));
    }

    @Test
    @DisplayName("GET /api/donations/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/donations/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/donations/user/{userId} devuelve donaciones del usuario")
    @WithMockUser
    void shouldGetDonationsByUser() throws Exception {
        when(donationService.findByUserId(1L)).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/donations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    // -------------------- GET /me --------------------

    @Test
    @DisplayName("GET /api/donations/me con usuario autenticado devuelve sus donaciones")
    @WithMockUser(username = "ana@test.com", roles = "USER")
    void shouldGetMyDonationsAsUser() throws Exception {
        when(donationService.findByUserEmail("ana@test.com")).thenReturn(List.of(buildDTO()));

        mockMvc.perform(get("/api/donations/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("GET /api/donations/me sin autenticación devuelve 403")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/donations/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/donations/category/{category} con ADMIN devuelve lista")
    @WithMockUser(roles = "ADMIN")
    void shouldGetDonationsByCategoryAsAdmin() throws Exception {
        when(donationService.findByCategory("MONETARIA")).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/donations/category/MONETARIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("MONETARIA"));
    }

    @Test
    @DisplayName("GET /api/donations/category/{category} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetByCategoryAsUser() throws Exception {
        mockMvc.perform(get("/api/donations/category/MONETARIA"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/donations/user/{userId}/total devuelve total del usuario")
    @WithMockUser
    void shouldGetTotalByUser() throws Exception {
        when(donationService.getTotalAmountByUser(1L)).thenReturn(BigDecimal.valueOf(150.00));

        mockMvc.perform(get("/api/donations/user/1/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(150.00));
    }

    @Test
    @DisplayName("GET /api/donations/total con ADMIN devuelve total global")
    @WithMockUser(roles = "ADMIN")
    void shouldGetTotalGlobalAsAdmin() throws Exception {
        when(donationService.getTotalDonationsAmount()).thenReturn(BigDecimal.valueOf(5000.00));

        mockMvc.perform(get("/api/donations/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5000.00));
    }

    @Test
    @DisplayName("GET /api/donations/total sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenGetTotalAsUser() throws Exception {
        mockMvc.perform(get("/api/donations/total"))
                .andExpect(status().isForbidden());
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/donations autenticado crea donación y devuelve 201")
    @WithMockUser
    void shouldCreateDonation() throws Exception {
        DonationDTO dto = buildDTO();
        when(donationService.save(any(DonationDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/donations/{id} con ADMIN elimina y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteDonationAsAdmin() throws Exception {
        doNothing().when(donationService).deleteById(1L);

        mockMvc.perform(delete("/api/donations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/donations/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteAsUser() throws Exception {
        mockMvc.perform(delete("/api/donations/1"))
                .andExpect(status().isForbidden());
    }
}