package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.AdoptionFormTokenService;
import com.example.cafe_con_huellas.service.EmailService;
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

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdoptionFormController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AdoptionFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdoptionFormTokenService tokenService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private AdoptionFormToken buildToken() {
        User user = new User();
        user.setFirstName("María");
        user.setLastName1("García");
        user.setEmail("maria@example.com");

        Pet pet = new Pet();
        pet.setName("Firu");
        pet.setBreed("Labrador");

        AdoptionFormToken token = new AdoptionFormToken();
        token.setUser(user);
        token.setPet(pet);
        token.setExpiresAt(LocalDateTime.now().plusDays(3));

        return token;
    }

    // -------------------- POST /send --------------------

    @Test
    @DisplayName("POST /api/adoption-form/send con ADMIN envía formulario y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldSendAdoptionFormAsAdmin() throws Exception {
        doNothing().when(tokenService).generateAndSendFormToken(1L, 1L);

        mockMvc.perform(post("/api/adoption-form/send")
                        .param("userId", "1")
                        .param("petId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/adoption-form/send sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenSendAsUser() throws Exception {
        mockMvc.perform(post("/api/adoption-form/send")
                        .param("userId", "1")
                        .param("petId", "1"))
                .andExpect(status().isForbidden());
    }

    // -------------------- GET /validate/{token} --------------------

    @Test
    @DisplayName("GET /api/adoption-form/validate/{token} con token válido devuelve datos del formulario")
    void shouldValidateTokenPublicly() throws Exception {
        when(tokenService.validateToken("abc123")).thenReturn(buildToken());

        mockMvc.perform(get("/api/adoption-form/validate/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("María García"))
                .andExpect(jsonPath("$.userEmail").value("maria@example.com"))
                .andExpect(jsonPath("$.petName").value("Firu"))
                .andExpect(jsonPath("$.petBreed").value("Labrador"));
    }

    // -------------------- POST /submit/{token} --------------------

    @Test
    @DisplayName("POST /api/adoption-form/submit/{token} con token válido envía formulario y devuelve 204")
    void shouldSubmitAdoptionFormPublicly() throws Exception {
        when(tokenService.validateToken("abc123")).thenReturn(buildToken());
        doNothing().when(emailService).notifyAdminAdoptionRequest(any(), any(), any());
        doNothing().when(emailService).confirmAdoptionRequestToUser(any(), any(), any());
        doNothing().when(tokenService).markTokenAsUsed("abc123");

        Map<String, String> request = Map.of(
                "address", "Calle Mayor 1",
                "city", "Jerez",
                "housingType", "PISO",
                "hasGarden", "NO",
                "hasOtherPets", "NO",
                "additionalInfo", "Tengo experiencia con perros"
        );

        mockMvc.perform(post("/api/adoption-form/submit/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}