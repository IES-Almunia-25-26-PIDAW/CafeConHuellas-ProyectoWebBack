package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import com.example.cafe_con_huellas.service.AdoptionRequestService;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * Obtiene el listado de solicitudes de adopción.
     * <p>
     * Si se proporciona el parámetro {@code email}, devuelve únicamente las solicitudes
     * del usuario con ese email. Si no se proporciona, devuelve todas las solicitudes.
     * Solo accesible para administradores.
     * </p>
     *
     * @param email email del usuario por el que filtrar (opcional)
     * @return lista de {@link AdoptionRequestDTO} con los registros encontrados
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdoptionRequestDTO> getAll(
            @RequestParam(required = false) String email) {
        if (email != null && !email.isBlank()) {
            return requestService.findByUserEmail(email);
        }
        return requestService.findAll();
    }

    /**
     * Devuelve las solicitudes de adopción del usuario autenticado.
     * <p>
     * No requiere rol específico: cualquier usuario con un token válido
     * puede consultar sus propias solicitudes. El backend identifica al usuario
     * a partir del email almacenado en el JWT (subject), nunca desde
     * un parámetro enviado por el cliente.
     * </p>
     *
     * @return lista de {@link AdoptionRequestDTO} del usuario autenticado
     */
    @GetMapping("/me")
    public List<AdoptionRequestDTO> getMyRequests() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return requestService.findByUserEmail(email);
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
