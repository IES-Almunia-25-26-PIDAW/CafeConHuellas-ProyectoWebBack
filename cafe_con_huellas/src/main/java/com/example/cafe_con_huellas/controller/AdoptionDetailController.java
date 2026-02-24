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
    private final AdoptionDetailMapper adoptionDetailMapper;

    // Obtiene todos los registros de detalles de adopción (Historial completo)
    @GetMapping
    public List<AdoptionDetailDTO> getAllDetails() {
        return adoptionDetailService.findAll().stream()
                .map(adoptionDetailMapper::toDto)
                .toList();
    }

    // Busca los detalles específicos de una adopción por su ID
    @GetMapping("/{id}")
    public AdoptionDetailDTO getDetailById(@PathVariable Long id) {
        return adoptionDetailMapper.toDto(adoptionDetailService.findById(id));
    }

    /* Registra los detalles técnicos de una adopción
     * Vincula el contrato con una relación usuario-mascota ya existente
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionDetailDTO createAdoptionDetail(@Valid @RequestBody AdoptionDetailDTO dto) {
        AdoptionDetail entity = adoptionDetailMapper.toEntity(dto);
        return adoptionDetailMapper.toDto(adoptionDetailService.save(entity));
    }

    /* Actualiza el seguimiento de la adopción
     * Útil para añadir notas sobre cómo se está adaptando la mascota o registrar incidencias
     */
    @PutMapping("/{id}")
    public AdoptionDetailDTO updateAdoptionDetail(@PathVariable Long id, @Valid @RequestBody AdoptionDetailDTO dto) {
        AdoptionDetail existing = adoptionDetailService.findById(id);

        // Actualizamos solo los campos de información, no los IDs de relación
        existing.setPlace(dto.getPlace());
        existing.setConditions(dto.getConditions());
        existing.setIssues(dto.getIssues());
        existing.setNotes(dto.getNotes());

        return adoptionDetailMapper.toDto(adoptionDetailService.save(existing));
    }

    // Elimina detalle adopción
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDetail(@PathVariable Long id) {
        adoptionDetailService.deleteById(id);
    }


}