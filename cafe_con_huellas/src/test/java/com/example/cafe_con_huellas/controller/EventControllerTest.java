package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.EventService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private EventDTO buildDTO() {
        return EventDTO.builder()
                .id(1L)
                .name("Feria de adopción")
                .description("Evento de adopción de animales en el refugio de Jerez")
                .eventDate(LocalDateTime.now().plusDays(10))
                .location("Jerez de la Frontera")
                .imageUrl("https://example.com/evento.jpg")
                .eventType("ADOPCION")
                .status("PROGRAMADO")
                .maxCapacity(100)
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/events devuelve lista de eventos")
    @WithMockUser
    void shouldGetAllEvents() throws Exception {
        when(eventService.findAll()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Feria de adopción"));
    }

    @Test
    @DisplayName("GET /api/events/{id} devuelve evento por ID")
    @WithMockUser
    void shouldGetEventById() throws Exception {
        when(eventService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Jerez de la Frontera"));
    }

    @Test
    @DisplayName("GET /api/events/upcoming devuelve próximos eventos")
    @WithMockUser
    void shouldGetUpcomingEvents() throws Exception {
        when(eventService.findUpcomingEvents()).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PROGRAMADO"));
    }

    @Test
    @DisplayName("GET /api/events/status/{status} devuelve eventos por estado")
    @WithMockUser
    void shouldGetEventsByStatus() throws Exception {
        when(eventService.findByStatus("PROGRAMADO")).thenAnswer(inv -> List.of(buildDTO()));

        mockMvc.perform(get("/api/events/status/PROGRAMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("ADOPCION"));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/events con ADMIN crea evento y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateEventAsAdmin() throws Exception {
        EventDTO dto = buildDTO();
        when(eventService.save(any(EventDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Feria de adopción"));
    }

    @Test
    @DisplayName("POST /api/events sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenCreateAsUser() throws Exception {
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- PUT --------------------

    @Test
    @DisplayName("PUT /api/events/{id} con ADMIN actualiza evento")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateEventAsAdmin() throws Exception {
        EventDTO dto = buildDTO();
        when(eventService.save(any(EventDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Feria de adopción"));
    }

    @Test
    @DisplayName("PUT /api/events/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUpdateAsUser() throws Exception {
        mockMvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/events/{id} con ADMIN elimina y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteEventAsAdmin() throws Exception {
        doNothing().when(eventService).deleteById(1L);

        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/events/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeleteAsUser() throws Exception {
        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isForbidden());
    }
}
