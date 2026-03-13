package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.mapper.UserPetRelationshipMapper;
import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.service.UserPetRelationshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los vínculos entre usuarios y mascotas.
 * <p>
 * Cubre los distintos tipos de relación del refugio: adopciones, casas de acogida,
 * voluntarios de paseo, etc. La mayoría de operaciones requieren rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
public class UserPetRelationshipController {

    private final UserPetRelationshipService relationshipService;

    /**
     * Obtiene el listado completo de todos los vínculos registrados en el sistema.
     * Requiere rol ADMIN.
     *
     * @return lista de {@link UserPetRelationshipDTO} con todos los registros
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserPetRelationshipDTO> getAllRelationships() {
        return relationshipService.findAll();
    }

    /**
     * Obtiene el detalle de un vínculo concreto por su identificador.
     * Requiere rol ADMIN.
     *
     * @param id identificador único del vínculo
     * @return {@link UserPetRelationshipDTO} con los datos del registro
     * @throws ResourceNotFoundException si no existe el vínculo con ese ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserPetRelationshipDTO getRelationshipById(@PathVariable Long id) {
        return relationshipService.findById(id);
    }

    /**
     * Obtiene los vínculos que están actualmente activos en el refugio.
     * Requiere rol ADMIN.
     *
     * @return lista de {@link UserPetRelationshipDTO} con los procesos activos
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserPetRelationshipDTO> getActiveRelationships() {
        return relationshipService.findActiveRelationships();
    }

    /**
     * Obtiene el historial de vínculos de un usuario específico.
     *
     * @param userId identificador del usuario
     * @return lista de {@link UserPetRelationshipDTO} asociados al usuario
     */
    @GetMapping("/user/{userId}")
    public List<UserPetRelationshipDTO> getRelationshipsByUser(@PathVariable Long userId) {
        return relationshipService.findByUserId(userId);
    }

    /**
     * Obtiene la trazabilidad completa de relaciones que ha tenido una mascota.
     *
     * @param petId identificador de la mascota
     * @return lista de {@link UserPetRelationshipDTO} asociados a la mascota
     */
    @GetMapping("/pet/{petId}")
    public List<UserPetRelationshipDTO> getRelationshipsByPet(@PathVariable Long petId) {
        return relationshipService.findByPetId(petId);
    }


    /**
     * Registra un nuevo vínculo entre un usuario y una mascota.
     * <p>
     * El servicio valida automáticamente la disponibilidad de la mascota antes de crear el vínculo.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param dto datos del vínculo a registrar
     * @return {@link UserPetRelationshipDTO} con el registro creado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserPetRelationshipDTO createRelationship(@Valid @RequestBody UserPetRelationshipDTO dto) {
        return relationshipService.save(dto);
    }

    /**
     * Finaliza un vínculo activo asignando la fecha de cierre.
     * <p>
     * Se usa para marcar el fin de una acogida, paseo u otro proceso activo.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param id identificador del vínculo a finalizar
     */
    @PatchMapping("/{id}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void endRelationship(@PathVariable Long id) {
        relationshipService.endRelationship(id, null);
    }

    /**
     * Elimina un registro de vínculo del sistema.
     * <p>
     * Reservado para correcciones administrativas de registros erróneos.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param id identificador del vínculo a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRelationship(@PathVariable Long id) {
        relationshipService.deleteById(id);
    }
}