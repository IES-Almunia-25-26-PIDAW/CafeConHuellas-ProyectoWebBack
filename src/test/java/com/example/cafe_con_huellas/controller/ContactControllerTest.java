package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.ContactRequestDTO;
import com.example.cafe_con_huellas.service.EmailService;
import com.example.cafe_con_huellas.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /api/contact con datos válidos envía email y devuelve 204")
    void shouldSendContactMessageSuccessfully() throws Exception {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNombre("Ana Cabello");
        dto.setEmail("ana@test.com");
        dto.setMensaje("Hola, tengo una pregunta sobre adopción");

        doNothing().when(emailService).notifyAdminContactForm(any(), any(), any());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/contact sin autenticación devuelve 204 (endpoint público)")
    void shouldAllowUnauthenticatedAccess() throws Exception {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNombre("Ana Cabello");
        dto.setEmail("ana@test.com");
        dto.setMensaje("Mensaje de prueba");

        doNothing().when(emailService).notifyAdminContactForm(any(), any(), any());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/contact con nombre vacío devuelve 400")
    void shouldReturn400WhenNombreIsBlank() throws Exception {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNombre("");
        dto.setEmail("ana@test.com");
        dto.setMensaje("Mensaje de prueba");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact con email inválido devuelve 400")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNombre("Ana Cabello");
        dto.setEmail("esto-no-es-un-email");
        dto.setMensaje("Mensaje de prueba");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact con mensaje vacío devuelve 400")
    void shouldReturn400WhenMensajeIsBlank() throws Exception {
        ContactRequestDTO dto = new ContactRequestDTO();
        dto.setNombre("Ana Cabello");
        dto.setEmail("ana@test.com");
        dto.setMensaje("");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}