package com.example.cafe_con_huellas.controller;


import com.example.cafe_con_huellas.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controlador REST para la gestión de archivos del sistema.
 * <p>
 * Proporciona endpoints para la subida de imágenes de perfil (avatares).
 * El archivo se guarda en disco local y se devuelve la URL pública
 * para que el frontend la asocie al usuario durante el registro
 * o la edición de perfil.
 * </p>
 * <p>
 * Endpoint público (configurado en {@code SecurityConfig}):
 * no requiere autenticación para permitir la subida antes del registro.
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
}