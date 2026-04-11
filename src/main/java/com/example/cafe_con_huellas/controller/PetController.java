package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.dto.PetSummaryDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de mascotas.
 * <p>
 * Incluye CRUD completo y filtros por categoría y estado de esterilización.
 * La consulta es pública; el alta, edición y borrado requieren rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Validated // Permite que las validaciones en los parámetros de los métodos funcionen
public class PetController {

    private final PetService petService;
    private final PetMapper petMapper;

    // -------------------- CRUD BÁSICO --------------------

    /**
     * Obtiene el catálogo completo de mascotas en formato resumido.
     * <p>
     * Devuelve una vista simplificada adecuada para la galería principal.
     * </p>
     *
     * @return lista de {@link PetSummaryDTO} con todas las mascotas registradas
     */
    @GetMapping
    public List<PetSummaryDTO> getAllPets() {
        // Obtenemos los detalles del servicio y los convertimos a resumen para la galería principal
        return petService.findAll().stream()
                .map((PetDetailDTO dto) -> petMapper.toSummaryDto(dto))
                .toList();
    }

    /**
     * Obtiene la ficha detallada de una mascota específica.
     *
     * @param id identificador único de la mascota
     * @return {@link PetDetailDTO} con toda la información de la mascota
     * @throws ResourceNotFoundException si no existe mascota con ese ID
     */
    @GetMapping("/{id}")
    public PetDetailDTO getPetById(@PathVariable @NotNull Long id) {
        return petService.findById(id);
    }

    /**
     * Registra una nueva mascota en el sistema.
     * Requiere rol ADMIN.
     *
     * @param petDetailDTO datos de la mascota a registrar
     * @return {@link PetDetailDTO} con la mascota persistida
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PetDetailDTO createPet(@Valid @RequestBody PetDetailDTO petDetailDTO) {
        return petService.save(petDetailDTO);
    }

    /**
     * Actualiza la información técnica y descriptiva de una mascota existente.
     * Requiere rol ADMIN.
     *
     * @param id           identificador de la mascota a actualizar
     * @param petDetailDTO nuevos datos de la mascota
     * @return {@link PetDetailDTO} con los datos actualizados
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PetDetailDTO updatePet(@PathVariable @NotNull Long id, @Valid @RequestBody PetDetailDTO petDetailDTO) {
        return petService.updateBasicInfo(id, petDetailDTO);
    }

    /**
     * Elimina el registro de una mascota del sistema.
     * Requiere rol ADMIN.
     *
     * @param id identificador de la mascota a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePet(@PathVariable @NotNull Long id) {
        petService.deleteById(id);
    }

    // -------------------- FILTROS --------------------

    /**
     * Filtra el catálogo de mascotas según su estado de esterilización.
     *
     * @param neutered {@code true} para mostrar solo esterilizadas, {@code false} para no esterilizadas
     * @return lista de {@link PetSummaryDTO} filtrada por estado de esterilización
     */
    @GetMapping("/filter/neutered")
    public List<PetSummaryDTO> getPetsByNeutered(@RequestParam Boolean neutered) {
        return petService.findByNeutered(neutered).stream()
                .map((PetDetailDTO dto) -> petMapper.toSummaryDto(dto))
                .toList();
    }

    /**
     * Filtra el catálogo de mascotas por categoría.
     *
     * @param category categoría por la que filtrar (ej: PERRO, GATO)
     * @return lista de {@link PetSummaryDTO} de la categoría indicada
     */
    @GetMapping("/filter/category")
    public List<PetSummaryDTO> getPetsByCategory(@RequestParam String category) {
        return petService.findByCategory(category).stream()
                .map((PetDetailDTO dto) -> petMapper.toSummaryDto(dto))
                .toList();
    }
}