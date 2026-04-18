package com.example.cafe_con_huellas.controller;


import com.example.cafe_con_huellas.config.SecurityConfig;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests del controlador {@link FileController}.
 * <p>
 * Verifica los endpoints de subida de imágenes:
 * </p>
 * <ul>
 *   <li>Avatar: endpoint público, no requiere autenticación.</li>
 *   <li>Imagen de mascota: requiere rol {@code ADMIN}, devuelve 403 si no autenticado o sin rol.</li>
 *   <li>Imagen de evento: requiere rol {@code ADMIN}, devuelve 403 si no autenticado o sin rol.</li>
 * </ul>
 */
@WebMvcTest(FileController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // -------------------- POST /api/files/upload-avatar --------------------

    @Test
    @DisplayName("POST /api/files/upload-avatar con imagen válida devuelve 201 y la URL")
    void shouldUploadAvatarSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagen de prueba".getBytes()
        );

        when(fileStorageService.saveAvatar(any())).thenReturn("http://localhost:8087/uploads/avatars/uuid-test.jpg");

        mockMvc.perform(multipart("/api/files/upload-avatar").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("http://localhost:8087/uploads/avatars/uuid-test.jpg"));
    }

    @Test
    @DisplayName("POST /api/files/upload-avatar con archivo vacío devuelve 400")
    void shouldReturn400WhenFileIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        when(fileStorageService.saveAvatar(any()))
                .thenThrow(new BadRequestException("El archivo está vacío"));

        mockMvc.perform(multipart("/api/files/upload-avatar").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/files/upload-avatar con extensión no permitida devuelve 400")
    void shouldReturn400WhenExtensionNotAllowed() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenido falso".getBytes()
        );

        when(fileStorageService.saveAvatar(any()))
                .thenThrow(new BadRequestException("Extensión no permitida. Usa: [jpg, jpeg, png, gif, webp]"));

        mockMvc.perform(multipart("/api/files/upload-avatar").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/files/upload-avatar sin archivo devuelve 400")
    void shouldReturn400WhenNoFileProvided() throws Exception {
        mockMvc.perform(multipart("/api/files/upload-avatar"))
                .andExpect(status().isBadRequest());
    }
    // -------------------- POST /api/files/upload-pet-image --------------------

    @Test
    @DisplayName("POST /api/files/upload-pet-image con ADMIN y imagen válida devuelve 201 y la URL")
    @WithMockUser(roles = "ADMIN")
    void shouldUploadPetImageAsAdmin() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "mascota.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagen de prueba".getBytes()
        );

        when(fileStorageService.savePetImage(any()))
                .thenReturn("http://localhost:8087/uploads/pets/uuid-test.jpg");

        mockMvc.perform(multipart("/api/files/upload-pet-image").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("http://localhost:8087/uploads/pets/uuid-test.jpg"));
    }

    @Test
    @DisplayName("POST /api/files/upload-pet-image con USER devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUploadPetImageAsUser() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "mascota.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagen de prueba".getBytes()
        );

        mockMvc.perform(multipart("/api/files/upload-pet-image").file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/files/upload-pet-image sin autenticación devuelve 403")
    void shouldReturn403WhenUploadPetImageWithoutAuth() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "mascota.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagen de prueba".getBytes()
        );

        mockMvc.perform(multipart("/api/files/upload-pet-image").file(file))
                .andExpect(status().isForbidden());
    }

// -------------------- POST /api/files/upload-event-image --------------------

    @Test
    @DisplayName("POST /api/files/upload-event-image con ADMIN y imagen válida devuelve 201 y la URL")
    @WithMockUser(roles = "ADMIN")
    void shouldUploadEventImageAsAdmin() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "evento.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagen de prueba".getBytes()
        );

        when(fileStorageService.saveEventImage(any()))
                .thenReturn("http://localhost:8087/uploads/events/uuid-test.jpg");

        mockMvc.perform(multipart("/api/files/upload-event-image").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("http://localhost:8087/uploads/events/uuid-test.jpg"));
    }

    @Test
    @DisplayName("POST /api/files/upload-event-image con USER devuelve 403")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUploadEventImageAsUser() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "evento.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagen de prueba".getBytes()
        );

        mockMvc.perform(multipart("/api/files/upload-event-image").file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/files/upload-event-image sin autenticación devuelve 403")
    void shouldReturn403WhenUploadEventImageWithoutAuth() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "evento.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagen de prueba".getBytes()
        );

        mockMvc.perform(multipart("/api/files/upload-event-image").file(file))
                .andExpect(status().isForbidden());
    }
}