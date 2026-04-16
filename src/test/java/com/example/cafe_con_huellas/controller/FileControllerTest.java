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
 * Verifica el endpoint de subida de avatares, incluyendo casos de éxito,
 * archivo vacío y extensión no permitida. El endpoint es público,
 * por lo que no se requiere autenticación en ningún caso.
 * </p>
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
}