package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.mapper.AdoptionDetailMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.service.AdoptionDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar la información post-adopción.
 * <p>
 * Permite al administrador registrar, consultar, actualizar y eliminar
 * los detalles técnicos y de seguimiento asociados a una adopción completada.
 * Todos los endpoints requieren rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/adoption-details")
@RequiredArgsConstructor
public class AdoptionDetailController {

    private final AdoptionDetailService adoptionDetailService;


    /**
     * Obtiene el listado completo de detalles de adopción registrados.
     *
     * @return lista de {@link AdoptionDetailDTO} con todos los registros
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdoptionDetailDTO> getAllDetails() {
        // El servicio ya devuelve una lista de DTOs
        return adoptionDetailService.findAll();
    }


    /**
     * Obtiene los detalles de una adopción concreta por su identificador.
     *
     * @param id identificador único del detalle de adopción
     * @return {@link AdoptionDetailDTO} con los datos del registro encontrado
     * @throws ResourceNotFoundException si no existe ningún detalle con ese ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO getDetailById(@PathVariable Long id) {
        // El servicio ya maneja la excepción y el mapeo a DTO
        return adoptionDetailService.findById(id);
    }

    /**
     * Registra los detalles técnicos de una adopción.
     * <p>
     * Recibe un DTO validado con la información post-adopción
     * y delega la persistencia al servicio correspondiente.
     * </p>
     *
     * @param dto datos del detalle de adopción a registrar
     * @return {@link AdoptionDetailDTO} con el registro persistido
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO createAdoptionDetail(@Valid @RequestBody AdoptionDetailDTO dto) {
        return adoptionDetailService.save(dto);
    }

    /**
     * Actualiza el seguimiento de una adopción existente.
     * <p>
     * Permite modificar notas, condiciones del animal y lugar de la adopción.
     * </p>
     *
     * @param id  identificador del detalle de adopción a actualizar
     * @param dto nuevos datos de seguimiento
     * @return {@link AdoptionDetailDTO} con los datos actualizados
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO updateAdoptionDetail(@PathVariable Long id, @Valid @RequestBody AdoptionDetailDTO dto) {
        return adoptionDetailService.updateDetails(id, dto);
    }

    /**
     * Obtiene los detalles de adopción asociados a una relación usuario-mascota específica.
     *
     * @param relationshipId identificador de la relación usuario-mascota
     * @return {@link AdoptionDetailDTO} con los detalles del seguimiento
     */
    @GetMapping("/relationship/{relationshipId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdoptionDetailDTO getByRelationshipId(@PathVariable Long relationshipId) {
        return adoptionDetailService.findByRelationshipId(relationshipId);
    }


    /**
     * Elimina un detalle de adopción del sistema.
     *
     * @param id identificador del detalle a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDetail(@PathVariable Long id) {
        adoptionDetailService.deleteById(id);
    }


}