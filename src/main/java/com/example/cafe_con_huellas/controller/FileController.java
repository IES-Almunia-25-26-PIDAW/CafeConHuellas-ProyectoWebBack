package com.example.cafe_con_huellas.controller;


import com.example.cafe_con_huellas.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controlador REST para la gestión de archivos del sistema.
 * <p>
 * Proporciona endpoints para la subida de imágenes:
 * </p>
 * <ul>
 *   <li>Avatar de usuario: público, para permitir la subida antes del registro.</li>
 *   <li>Imagen de mascota: requiere rol {@code ADMIN}.</li>
 *   <li>Imagen de evento: requiere rol {@code ADMIN}.</li>
 * </ul>
 * <p>
 * Los archivos se guardan en disco local bajo {@code uploads/} y se devuelve
 * la URL pública para que el frontend la asocie a la entidad correspondiente.
 * </p>
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Sube una imagen de avatar y devuelve su URL pública.
     * <p>
     * El frontend envía el archivo como {@code multipart/form-data}
     * con el campo {@code file}. El servicio valida el archivo
     * y lo almacena en disco con un nombre UUID único.
     * </p>
     * <p>Ejemplo de uso desde el frontend:</p>
     * <pre>
     *   POST /api/files/upload-avatar
     *   Content-Type: multipart/form-data
     *   Body: file = [imagen seleccionada]
     *
     *   Respuesta 201:
     *   { "imageUrl": "/uploads/avatars/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg" }
     * </pre>
     *
     * @param file archivo de imagen enviado como {@link MultipartFile}
     * @return mapa con la clave {@code imageUrl} y la ruta pública del archivo guardado
     */
    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.saveAvatar(file);
        return Map.of("imageUrl", url);
    }

    /**
     * Sube una imagen de mascota y devuelve su URL pública.
     * <p>
     * Solo accesible por usuarios con rol {@code ADMIN}.
     * El frontend envía el archivo como {@code multipart/form-data}
     * con el campo {@code file}. El servicio valida el archivo
     * y lo almacena en disco con un nombre UUID único.
     * </p>
     * <p>Ejemplo de uso desde el frontend:</p>
     * <pre>
     *   POST /api/files/upload-pet-image
     *   Authorization: Bearer {token}
     *   Content-Type: multipart/form-data
     *   Body: file = [imagen seleccionada]
     *
     *   Respuesta 201:
     *   { "imageUrl": "http://localhost:8087/uploads/pets/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg" }
     * </pre>
     *
     * @param file archivo de imagen enviado como {@link MultipartFile}
     * @return mapa con la clave {@code imageUrl} y la URL pública del archivo guardado
     */
    @PostMapping(value = "/upload-pet-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> uploadPetImage(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.savePetImage(file);
        return Map.of("imageUrl", url);
    }

    /**
     * Sube una imagen de evento y devuelve su URL pública.
     * <p>
     * Solo accesible por usuarios con rol {@code ADMIN}.
     * El frontend envía el archivo como {@code multipart/form-data}
     * con el campo {@code file}. El servicio valida el archivo
     * y lo almacena en disco con un nombre UUID único.
     * </p>
     * <p>Ejemplo de uso desde el frontend:</p>
     * <pre>
     *   POST /api/files/upload-event-image
     *   Authorization: Bearer {token}
     *   Content-Type: multipart/form-data
     *   Body: file = [imagen seleccionada]
     *
     *   Respuesta 201:
     *   { "imageUrl": "http://localhost:8087/uploads/events/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg" }
     * </pre>
     *
     * @param file archivo de imagen enviado como {@link MultipartFile}
     * @return mapa con la clave {@code imageUrl} y la URL pública del archivo guardado
     */
    @PostMapping(value = "/upload-event-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> uploadEventImage(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.saveEventImage(file);
        return Map.of("imageUrl", url);
    }
}