package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.mapper.AdoptionDetailMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import com.example.cafe_con_huellas.service.AdoptionDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* Controlador REST para gestionar la información post-adopción
 * Permite registrar y consultar los detalles técnicos y de seguimiento de una adopción
 */
@RestController
@RequestMapping("/api/adoption-details")
@RequiredArgsConstructor
public class AdoptionDetailController {

    private final AdoptionDetailService adoptionDetailService;


    // Solo ADMIN puede ver el historial completo de detalles de adopción
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdoptionDetailDTO> getAllDetails() {
        // El servicio ya devuelve una lista de DTOs
        return adoptionDetailService.findAll();
    }


    // Solo ADMIN puede ver los detalles de una adopción concreta
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO getDetailById(@PathVariable Long id) {
        // El servicio ya maneja la excepción y el mapeo a DTO
        return adoptionDetailService.findById(id);
    }

    /* * Registra los detalles técnicos de una adopción.
     * Recibe un DTO validado y delega la persistencia al servicio.
     */
    // Solo ADMIN puede registrar detalles técnicos de una adopción
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO createAdoptionDetail(@Valid @RequestBody AdoptionDetailDTO dto) {
        return adoptionDetailService.save(dto);
    }

    /* * Actualiza el seguimiento de la adopción (notas, condiciones, lugar).
     * Utiliza el método de actualización controlada del servicio.
     */
    // Solo ADMIN puede actualizar el seguimiento de una adopción
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO updateAdoptionDetail(@PathVariable Long id, @Valid @RequestBody AdoptionDetailDTO dto) {
        return adoptionDetailService.updateDetails(id, dto);
    }

    // Solo ADMIN puede buscar detalles por relación usuario-mascota específica
    @GetMapping("/relationship/{relationshipId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO getByRelationshipId(@PathVariable Long relationshipId) {
        return adoptionDetailService.findByRelationshipId(relationshipId);
    }


    // Solo ADMIN puede eliminar detalles de una adopción
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDetail(@PathVariable Long id) {
        adoptionDetailService.deleteById(id);
    }


}