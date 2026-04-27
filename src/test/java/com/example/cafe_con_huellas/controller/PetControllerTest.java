package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.mapper.PetMapperImpl;
import com.example.cafe_con_huellas.model.entity.AdoptionStatus;
import com.example.cafe_con_huellas.service.PetService;
import com.example.cafe_con_huellas.security.JwtService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@Import({SecurityConfig.class, PetMapperImpl.class})
@ActiveProfiles("test")
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private PetDetailDTO buildPetDetailDTO() {
        return PetDetailDTO.builder()
                .id(1L)
                .name("Firu")
                .description("Perro muy juguetón y cariñoso")
                .breed("Labrador")
                .category("PERRO")
                .age(3)
                .weight(BigDecimal.valueOf(25.0))
                .neutered(true)
                .isPpp(false)
                .urgentAdoption(false)
                .adoptionStatus(AdoptionStatus.NO_ADOPTADO)
                .imageUrl("https://example.com/firu.jpg")
                .build();
    }

    // -------------------- GET --------------------

    @Test
    @DisplayName("GET /api/pets devuelve lista de mascotas")
    @WithMockUser
    void shouldGetAllPets() throws Exception {
        when(petService.findAll()).thenAnswer(inv -> List.of(buildPetDetailDTO()));

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Firu"))
                .andExpect(jsonPath("$[0].description").value("Perro muy juguetón y cariñoso"));
    }

    @Test
    @DisplayName("GET /api/pets/{id} devuelve detalle de mascota")
    @WithMockUser
    void shouldGetPetById() throws Exception {
        when(petService.findById(1L)).thenReturn(buildPetDetailDTO());

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Firu"))
                .andExpect(jsonPath("$.breed").value("Labrador"));
    }

    @Test
    @DisplayName("GET /api/pets sin autenticación devuelve 200")
    void shouldGetAllPetsUnauthenticated() throws Exception {
        when(petService.findAll()).thenAnswer(inv -> List.of(buildPetDetailDTO()));

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pets/{id} sin autenticación devuelve 200")
    void shouldGetPetByIdUnauthenticated() throws Exception {
        when(petService.findById(1L)).thenReturn(buildPetDetailDTO());

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pets/filter/neutered sin autenticación devuelve 200")
    void shouldGetPetsByNeuteredUnauthenticated() throws Exception {
        when(petService.findByNeutered(true)).thenAnswer(inv -> List.of(buildPetDetailDTO()));

        mockMvc.perform(get("/api/pets/filter/neutered").param("neutered", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pets/filter/category sin autenticación devuelve 200")
    void shouldGetPetsByCategoryUnauthenticated() throws Exception {
        when(petService.findByCategory("PERRO")).thenAnswer(inv -> List.of(buildPetDetailDTO()));

        mockMvc.perform(get("/api/pets/filter/category").param("category", "PERRO"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pets/{id} devuelve adoptionStatus correcto")
    @WithMockUser
    void shouldReturnAdoptionStatus() throws Exception {
        when(petService.findById(1L)).thenReturn(buildPetDetailDTO());

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adoptionStatus").value("NO_ADOPTADO"));
    }

    @Test
    @DisplayName("GET /api/pets/filter/adoption-status devuelve mascotas filtradas")
    @WithMockUser
    void shouldGetPetsByAdoptionStatus() throws Exception {
        when(petService.findByAdoptionStatus("NO_ADOPTADO")).thenAnswer(inv -> List.of(buildPetDetailDTO()));

        mockMvc.perform(get("/api/pets/filter/adoption-status").param("status", "NO_ADOPTADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adoptionStatus").value("NO_ADOPTADO"));
    }

    // -------------------- POST --------------------

    @Test
    @DisplayName("POST /api/pets con ADMIN crea mascota y devuelve 201")
    @WithMockUser(roles = "ADMIN")
    void shouldCreatePetAsAdmin() throws Exception {
        PetDetailDTO dto = buildPetDetailDTO();
        when(petService.save(any(PetDetailDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Firu"));
    }

    @Test
    @DisplayName("POST /api/pets sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenCreatePetAsUser() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildPetDetailDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/pets sin autenticación devuelve 403")
    void shouldReturn403WhenCreatePetUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildPetDetailDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/pets sin adoptionStatus devuelve 400")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenAdoptionStatusIsMissing() throws Exception {
        PetDetailDTO dto = buildPetDetailDTO();
        dto.setAdoptionStatus(null);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // -------------------- PUT --------------------

    @Test
    @DisplayName("PUT /api/pets/{id} con ADMIN actualiza mascota")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdatePetAsAdmin() throws Exception {
        PetDetailDTO dto = buildPetDetailDTO();
        when(petService.updateBasicInfo(eq(1L), any(PetDetailDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Firu"));
    }

    @Test
    @DisplayName("PUT /api/pets/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUpdatePetAsUser() throws Exception {
        mockMvc.perform(put("/api/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildPetDetailDTO())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @DisplayName("DELETE /api/pets/{id} con ADMIN elimina mascota y devuelve 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeletePetAsAdmin() throws Exception {
        doNothing().when(petService).deleteById(1L);

        mockMvc.perform(delete("/api/pets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/pets/{id} sin ADMIN devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenDeletePetAsUser() throws Exception {
        mockMvc.perform(delete("/api/pets/1"))
                .andExpect(status().isForbidden());
    }

    // -------------------- FILTROS --------------------

    @Test
    @DisplayName("GET /api/pets/filter/neutered devuelve mascotas filtradas")
    @WithMockUser
    void shouldGetPetsByNeutered() throws Exception {
        when(petService.findByNeutered(true)).thenAnswer(inv -> List.of(buildPetDetailDTO()));

        mockMvc.perform(get("/api/pets/filter/neutered").param("neutered", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].neutered").value(true));
    }

    @Test
    @DisplayName("GET /api/pets/filter/category devuelve mascotas por categoría")
    @WithMockUser
    void shouldGetPetsByCategory() throws Exception {
        when(petService.findByCategory("PERRO")).thenAnswer(inv -> List.of(buildPetDetailDTO()));

        mockMvc.perform(get("/api/pets/filter/category").param("category", "PERRO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("PERRO"));
    }


}