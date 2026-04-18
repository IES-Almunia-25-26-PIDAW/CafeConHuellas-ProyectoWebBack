package com.example.cafe_con_huellas.service;
import com.example.cafe_con_huellas.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Servicio encargado del almacenamiento de archivos de imagen en disco local.
 * <p>
 * Gestiona la subida de imágenes del sistema, validando tipo, extensión
 * y tamaño del archivo antes de guardarlo en la subcarpeta correspondiente:
 * </p>
 * <ul>
 *   <li>Avatares de usuario: {@code uploads/avatars/}</li>
 *   <li>Imágenes de mascotas: {@code uploads/pets/}</li>
 *   <li>Imágenes de eventos: {@code uploads/events/}</li>
 * </ul>
 * <p>Genera nombres únicos mediante UUID para evitar colisiones.</p>
 */
@Service
public class FileStorageService {
    /** Ruta del directorio donde se almacenan los avatares. */
    private static final Path AVATAR_DIR = Paths.get("uploads/avatars");

    /** Ruta del directorio donde se almacenan las imágenes de mascotas. */
    private static final Path PET_DIR = Paths.get("uploads/pets");

    /** Ruta del directorio donde se almacenan las imágenes de eventos. */
    private static final Path EVENT_DIR = Paths.get("uploads/events");

    /** Extensiones de imagen permitidas para la subida. */
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    /** Tamaño máximo permitido por archivo: 5 MB. */
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    /**
     * URL base del backend, leída desde {@code app.base-url} en application.properties.
     * Se usa para construir la URL pública completa del archivo subido,
     * de forma que el frontend pueda acceder directamente a la imagen sin
     * tener que conocer ni construir la URL del servidor.
     */
    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Guarda una imagen de avatar en disco y devuelve su URL pública completa.
     * <p>Realiza las siguientes validaciones antes de persistir el archivo:</p>
     * <ul>
     *   <li>Que el archivo no esté vacío.</li>
     *   <li>Que no supere el tamaño máximo de 5 MB.</li>
     *   <li>Que la extensión sea una de las permitidas (jpg, jpeg, png, gif, webp).</li>
     *   <li>Que el content-type corresponda a una imagen.</li>
     * </ul>
     * <p>El archivo se guarda con un nombre UUID único para evitar colisiones.</p>
     *
     * @param file archivo {@link MultipartFile} recibido desde el controlador
     * @return URL pública del archivo guardado, por ejemplo
     *         {@code http://localhost:8087/uploads/avatars/a1b2c3d4.jpg}
     * @throws BadRequestException si el archivo está vacío, supera el tamaño, tiene extensión
     *                             no permitida o no es una imagen válida
     * @throws RuntimeException    si ocurre un error de entrada/salida al escribir en disco
     */
    public String saveAvatar(MultipartFile file) {
        // Validar que no esté vacío
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        // Validar tamaño
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("El archivo supera el tamaño máximo de 5 MB");
        }

        // Validar extensión
        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Extensión no permitida. Usa: " + ALLOWED_EXTENSIONS);
        }

        // Validar que realmente sea una imagen por su content-type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("El archivo no es una imagen válida");
        }

        try {
            // Crear la carpeta si no existe
            Files.createDirectories(AVATAR_DIR);

            // Nombre único para evitar colisiones: UUID + extensión original
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = AVATAR_DIR.resolve(fileName);

            // Copiar el archivo al disco
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Devolver la URL pública completa incluyendo la base del servidor,
            // para que el frontend pueda usarla directamente sin transformaciones
            return baseUrl + "/uploads/avatars/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
}


    /**
     * Guarda una imagen de mascota en disco y devuelve su URL pública completa.
     * <p>Realiza las mismas validaciones que {@link #saveAvatar(MultipartFile)}:</p>
     * <ul>
     *   <li>Que el archivo no esté vacío.</li>
     *   <li>Que no supere el tamaño máximo de 5 MB.</li>
     *   <li>Que la extensión sea una de las permitidas (jpg, jpeg, png, gif, webp).</li>
     *   <li>Que el content-type corresponda a una imagen.</li>
     * </ul>
     * <p>El archivo se guarda en {@code uploads/pets/} con un nombre UUID único.</p>
     *
     * @param file archivo {@link MultipartFile} recibido desde el controlador
     * @return URL pública del archivo guardado, por ejemplo
     *         {@code http://localhost:8087/uploads/pets/a1b2c3d4.jpg}
     * @throws BadRequestException si el archivo está vacío, supera el tamaño, tiene extensión
     *                             no permitida o no es una imagen válida
     * @throws RuntimeException    si ocurre un error de entrada/salida al escribir en disco
     */
    public String savePetImage(MultipartFile file) {
        // Validar que no esté vacío
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        // Validar tamaño
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("El archivo supera el tamaño máximo de 5 MB");
        }

        // Validar extensión
        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Extensión no permitida. Usa: " + ALLOWED_EXTENSIONS);
        }

        // Validar que realmente sea una imagen por su content-type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("El archivo no es una imagen válida");
        }

        try {
            // Crear la carpeta si no existe
            Files.createDirectories(PET_DIR);

            // Nombre único para evitar colisiones: UUID + extensión original
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = PET_DIR.resolve(fileName);

            // Copiar el archivo al disco
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Devolver la URL pública completa incluyendo la base del servidor,
            // para que el frontend pueda usarla directamente sin transformaciones
            return baseUrl + "/uploads/pets/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Guarda una imagen de evento en disco y devuelve su URL pública completa.
     * <p>Realiza las mismas validaciones que {@link #saveAvatar(MultipartFile)}:</p>
     * <ul>
     *   <li>Que el archivo no esté vacío.</li>
     *   <li>Que no supere el tamaño máximo de 5 MB.</li>
     *   <li>Que la extensión sea una de las permitidas (jpg, jpeg, png, gif, webp).</li>
     *   <li>Que el content-type corresponda a una imagen.</li>
     * </ul>
     * <p>El archivo se guarda en {@code uploads/events/} con un nombre UUID único.</p>
     *
     * @param file archivo {@link MultipartFile} recibido desde el controlador
     * @return URL pública del archivo guardado, por ejemplo
     *         {@code http://localhost:8087/uploads/events/a1b2c3d4.jpg}
     * @throws BadRequestException si el archivo está vacío, supera el tamaño, tiene extensión
     *                             no permitida o no es una imagen válida
     * @throws RuntimeException    si ocurre un error de entrada/salida al escribir en disco
     */
    public String saveEventImage(MultipartFile file) {
        // Validar que no esté vacío
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        // Validar tamaño
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("El archivo supera el tamaño máximo de 5 MB");
        }

        // Validar extensión
        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Extensión no permitida. Usa: " + ALLOWED_EXTENSIONS);
        }

        // Validar que realmente sea una imagen por su content-type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("El archivo no es una imagen válida");
        }

        try {
            // Crear la carpeta si no existe
            Files.createDirectories(EVENT_DIR);

            // Nombre único para evitar colisiones: UUID + extensión original
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = EVENT_DIR.resolve(fileName);

            // Copiar el archivo al disco
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Devolver la URL pública completa incluyendo la base del servidor,
            // para que el frontend pueda usarla directamente sin transformaciones
            return baseUrl + "/uploads/events/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae la extensión de un nombre de archivo.
     *
     * @param fileName nombre original del archivo, por ejemplo {@code foto.jpg}
     * @return extensión sin el punto, por ejemplo {@code jpg}
     * @throws BadRequestException si el archivo no tiene extensión
     */
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new BadRequestException("El archivo no tiene extensión");
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
