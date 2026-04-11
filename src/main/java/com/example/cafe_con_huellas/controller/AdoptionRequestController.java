package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import com.example.cafe_con_huellas.service.AdoptionRequestService;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de solicitudes de adopción.
 * <p>
 * Permite al administrador consultar todas las solicitudes recibidas
 * a través del formulario público y gestionar su estado (aprobar/rechazar).
 * Todos los endpoints requieren rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/adoption-requests")
@RequiredArgsConstructor
public class AdoptionRequestController {

    private final AdoptionRequestService requestService;

    /**
     * Obtiene el listado completo de todas las solicitudes de adopción.
     *
     * @return lista de {@link AdoptionRequestDTO} con todos los registros
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdoptionRequestDTO> getAll() {
        return requestService.findAll();
    }

    /**
     * Filtra las solicitudes de adopción por su estado actual.
     *
     * @param status estado por el que filtrar ({@link AdoptionRequestStatus})
     * @return lista de {@link AdoptionRequestDTO} que coinciden con el estado indicado
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdoptionRequestDTO> getByStatus(@PathVariable AdoptionRequestStatus status) {
        return requestService.findByStatus(status);
    }

    /**
     * Obtiene el detalle de una solicitud de adopción concreta.
     *
     * @param id identificador único de la solicitud
     * @return {@link AdoptionRequestDTO} con los datos de la solicitud
     * @throws ResourceNotFoundException si no existe solicitud con ese ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionRequestDTO getById(@PathVariable Long id) {
        return requestService.findById(id);
    }

    /**
     * Actualiza el estado de una solicitud de adopción (aprobar o rechazar).
     *
     * @param id     identificador de la solicitud a actualizar
     * @param status nuevo estado a asignar ({@link AdoptionRequestStatus})
     * @return {@link AdoptionRequestDTO} con el estado actualizado
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionRequestDTO updateStatus(
            @PathVariable Long id,
            @RequestParam AdoptionRequestStatus status) {
        return requestService.updateStatus(id, status);
    }
}
