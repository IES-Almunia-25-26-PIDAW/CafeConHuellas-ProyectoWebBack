package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.mapper.VaccineMapper;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.service.VaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de vacunas.
 * <p>
 * Permite definir qué vacunas existen en el sistema para poder
 * asignarlas posteriormente a las mascotas. La consulta es pública;
 * la creación y eliminación requieren rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService vaccineService;

    /**
     * Obtiene el catálogo completo de vacunas disponibles en el sistema.
     *
     * @return lista de {@link VaccineDTO} con todas las vacunas registradas
     */
    @GetMapping
    public List<VaccineDTO> getAllVaccines() {
        // El service ya devuelve la lista mapeada a DTO
        return vaccineService.findAll();
    }

    /**
     * Obtiene los detalles de una vacuna específica por su identificador.
     *
     * @param id identificador único de la vacuna
     * @return {@link VaccineDTO} con los datos de la vacuna
     * @throws ResourceNotFoundException si no existe vacuna con ese ID
     */
    @GetMapping("/{id}")
    public VaccineDTO getVaccineById(@PathVariable Long id) {
        return vaccineService.findById(id);
    }


    /**
     * Añade una nueva vacuna al catálogo del sistema.
     * <p>
     * El servicio valida que no exista otra vacuna con el mismo nombre.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param vaccineDTO datos de la vacuna a registrar
     * @return {@link VaccineDTO} con la vacuna persistida
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public VaccineDTO createVaccine(@Valid @RequestBody VaccineDTO vaccineDTO) {
        // Pasamos el DTO directo al service para que gestione el mapeo y persistencia
        return vaccineService.save(vaccineDTO);
    }


    /**
     * Elimina una vacuna del catálogo del sistema.
     * Requiere rol ADMIN.
     *
     * @param id identificador de la vacuna a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVaccine(@PathVariable Long id) {
        vaccineService.deleteById(id);
    }
}