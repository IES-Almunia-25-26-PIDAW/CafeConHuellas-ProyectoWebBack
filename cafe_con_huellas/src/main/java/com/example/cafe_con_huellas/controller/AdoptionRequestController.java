package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import com.example.cafe_con_huellas.service.AdoptionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-requests")
@RequiredArgsConstructor
public class AdoptionRequestController {

    private final AdoptionRequestService requestService;

    // Solo ADMIN: listado completo de solicitudes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdoptionRequestDTO> getAll() {
        return requestService.findAll();
    }

    // Solo ADMIN: filtrar por estado
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdoptionRequestDTO> getByStatus(@PathVariable AdoptionRequestStatus status) {
        return requestService.findByStatus(status);
    }

    // Solo ADMIN: detalle de una solicitud
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionRequestDTO getById(@PathVariable Long id) {
        return requestService.findById(id);
    }

    // Solo ADMIN: aprobar o rechazar una solicitud
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionRequestDTO updateStatus(
            @PathVariable Long id,
            @RequestParam AdoptionRequestStatus status) {
        return requestService.updateStatus(id, status);
    }
}
