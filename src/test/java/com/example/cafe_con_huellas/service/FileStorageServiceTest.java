package com.example.cafe_con_huellas.service;


import com.example.cafe_con_huellas.exception.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios del servicio {@link FileStorageService}.
 * <p>
 * Verifica la lógica de almacenamiento de avatares en disco,
 * incluyendo validaciones de archivo vacío, tamaño máximo,
 * extensiones permitidas y content-type.
 * Cada test limpia los archivos creados en {@code uploads/avatars/}
 * para no dejar residuos en el sistema de archivos.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    /** Ruta de la carpeta de subida usada por el servicio. */
    private static final Path UPLOAD_DIR = Paths.get("uploads/avatars");

    /**
     * Limpia la carpeta de uploads después de cada test
     * para no dejar archivos residuales.
     */
    @AfterEach
    void cleanUp() throws IOException {
        if (Files.exists(UPLOAD_DIR)) {
            Files.walkFileTree(UPLOAD_DIR, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            // Limpiar también la carpeta padre "uploads" si queda vacía
            Path uploadsDir = Paths.get("uploads");
            if (Files.exists(uploadsDir) && Files.list(uploadsDir).findAny().isEmpty()) {
                Files.delete(uploadsDir);
            }
        }
    }

    // -------------------- CASOS DE ÉXITO --------------------

    @Test
    @DisplayName("Guarda avatar JPG correctamente y devuelve ruta pública")
    void shouldSaveJpgAvatarSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenido imagen jpg".getBytes()
        );

        String result = fileStorageService.saveAvatar(file);

        assertThat(result).startsWith("/uploads/avatars/");
        assertThat(result).endsWith(".jpg");

        // Verificar que el archivo existe físicamente en disco
        String fileName = result.replace("/uploads/avatars/", "");
        assertThat(Files.exists(UPLOAD_DIR.resolve(fileName))).isTrue();
    }

    @Test
    @DisplayName("Guarda avatar PNG correctamente")
    void shouldSavePngAvatarSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.png",
                "image/png",
                "contenido imagen png".getBytes()
        );

        String result = fileStorageService.saveAvatar(file);

        assertThat(result).startsWith("/uploads/avatars/");
        assertThat(result).endsWith(".png");
    }

    // -------------------- VALIDACIONES --------------------

    @Test
    @DisplayName("Lanza BadRequestException si el archivo es null")
    void shouldThrowWhenFileIsNull() {
        assertThatThrownBy(() -> fileStorageService.saveAvatar(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El archivo está vacío");
    }

    @Test
    @DisplayName("Lanza BadRequestException si el archivo está vacío")
    void shouldThrowWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vacio.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        assertThatThrownBy(() -> fileStorageService.saveAvatar(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El archivo está vacío");
    }

    @Test
    @DisplayName("Lanza BadRequestException si el archivo supera 5 MB")
    void shouldThrowWhenFileTooLarge() {
        // Crear un array de 6 MB
        byte[] largeContent = new byte[6 * 1024 * 1024];

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "grande.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                largeContent
        );

        assertThatThrownBy(() -> fileStorageService.saveAvatar(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El archivo supera el tamaño máximo de 5 MB");
    }

    @Test
    @DisplayName("Lanza BadRequestException si la extensión no está permitida")
    void shouldThrowWhenExtensionNotAllowed() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenido falso".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.saveAvatar(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Extensión no permitida");
    }

    @Test
    @DisplayName("Lanza BadRequestException si el content-type no es imagen")
    void shouldThrowWhenContentTypeNotImage() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo.jpg",
                "text/plain",
                "esto no es una imagen".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.saveAvatar(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El archivo no es una imagen válida");
    }

    @Test
    @DisplayName("Lanza BadRequestException si el archivo no tiene extensión")
    void shouldThrowWhenFileHasNoExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sinextension",
                MediaType.IMAGE_JPEG_VALUE,
                "contenido".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.saveAvatar(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El archivo no tiene extensión");
    }
}