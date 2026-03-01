package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.mapper.UserPetRelationshipMapper;
import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.service.UserPetRelationshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* Controlador para gestionar los vínculos entre usuarios y mascotas.
 * Maneja procesos de adopción, casas de acogida, voluntarios de paseo...
 */
@RestController
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
public class UserPetRelationshipController {

    private final UserPetRelationshipService relationshipService;

    // Lista todos los vínculos registrados en el sistema (historial y activos)
    @GetMapping
    public List<UserPetRelationshipDTO> getAllRelationships() {
        return relationshipService.findAll();
    }

    // Busca una relación específica mediante su identificador único
    @GetMapping("/{id}")
    public UserPetRelationshipDTO getRelationshipById(@PathVariable Long id) {
        return relationshipService.findById(id);
    }

    // Filtra y devuelve solo los procesos que están marcados como activos actualmente
    @GetMapping("/active")
    public List<UserPetRelationshipDTO> getActiveRelationships() {
        return relationshipService.findActiveRelationships();
    }

    // Obtiene el historial de relaciones de un usuario específico
    @GetMapping("/user/{userId}")
    public List<UserPetRelationshipDTO> getRelationshipsByUser(@PathVariable Long userId) {
        return relationshipService.findByUserId(userId);
    }

    // Obtiene la trazabilidad de relaciones que ha tenido una mascota
    @GetMapping("/pet/{petId}")
    public List<UserPetRelationshipDTO> getRelationshipsByPet(@PathVariable Long petId) {
        return relationshipService.findByPetId(petId);
    }

    /* * Registra un nuevo vínculo (ej. se inicia un proceso de adopción o acogida).
     * El servicio valida automáticamente la disponibilidad de la mascota.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPetRelationshipDTO createRelationship(@Valid @RequestBody UserPetRelationshipDTO dto) {
        return relationshipService.save(dto);
    }

    /* * Finaliza una relación activa (ej. marcar el fin de una casa de acogida o paseo).
     * Establece el estado 'active' a false y asigna la fecha de cierre.
     */
    @PatchMapping("/{id}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void endRelationship(@PathVariable Long id) {
        relationshipService.endRelationship(id, null);
    }

    // Elimina un registro de relación (Solo para correcciones administrativas)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRelationship(@PathVariable Long id) {
        relationshipService.deleteById(id);
    }
}