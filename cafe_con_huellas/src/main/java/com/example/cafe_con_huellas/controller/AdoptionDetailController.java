package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.mapper.AdoptionDetailMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import com.example.cafe_con_huellas.service.AdoptionDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // Obtiene todos los registros de detalles de adopción (Historial completo)
    @GetMapping
    public List<AdoptionDetailDTO> getAllDetails() {
        // El servicio ya devuelve una lista de DTOs
        return adoptionDetailService.findAll();
    }

    // Busca los detalles específicos de una adopción por su identificador único
    @GetMapping("/{id}")
    public AdoptionDetailDTO getDetailById(@PathVariable Long id) {
        // El servicio ya maneja la excepción y el mapeo a DTO
        return adoptionDetailService.findById(id);
    }

    /* * Registra los detalles técnicos de una adopción.
     * Recibe un DTO validado y delega la persistencia al servicio.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionDetailDTO createAdoptionDetail(@Valid @RequestBody AdoptionDetailDTO dto) {
        return adoptionDetailService.save(dto);
    }

    /* * Actualiza el seguimiento de la adopción (notas, condiciones, lugar).
     * Utiliza el método de actualización controlada del servicio.
     */
    @PutMapping("/{id}")
    public AdoptionDetailDTO updateAdoptionDetail(@PathVariable Long id, @Valid @RequestBody AdoptionDetailDTO dto) {
        return adoptionDetailService.updateDetails(id, dto);
    }

    // Busca detalles asociados a una relación usuario-mascota específica
    @GetMapping("/relationship/{relationshipId}")
    public AdoptionDetailDTO getByRelationshipId(@PathVariable Long relationshipId) {
        return adoptionDetailService.findByRelationshipId(relationshipId);
    }

    // Elimina el registro de detalles de una adopción del sistema
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDetail(@PathVariable Long id) {
        adoptionDetailService.deleteById(id);
    }


}