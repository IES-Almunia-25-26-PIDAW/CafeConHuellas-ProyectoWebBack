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
    private final UserPetRelationshipMapper relationshipMapper;

    // Lista todos los vínculos (historial y activos)
    @GetMapping
    public List<UserPetRelationshipDTO> getAllRelationships() {
        return relationshipService.findAll().stream()
                .map(relationshipMapper::toDto)
                .toList();
    }

    // Filtra y devuelve solo los procesos que están activos actualmente
    @GetMapping("/active")
    public List<UserPetRelationshipDTO> getActiveRelationships() {
        return relationshipService.findActiveRelationships().stream()
                .map(relationshipMapper::toDto)
                .toList();
    }

    /* Registra un nuevo vínculo (ej. se inicia un proceso de adopción).
     * El sistema vincula el ID del usuario con el ID de la mascota.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPetRelationshipDTO createRelationship(@Valid @RequestBody UserPetRelationshipDTO dto) {
        UserPetRelationship entity = relationshipMapper.toEntity(dto);
        return relationshipMapper.toDto(relationshipService.save(entity));
    }

    /*
     * Finaliza una relación activa (ej. marcar el fin de una casa de acogida).
     */
    @PatchMapping("/{id}/end")
    public void endRelationship(@PathVariable Long id) {
        relationshipService.endRelationship(id, null);
    }
}