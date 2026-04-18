package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.DonationDTO;
import com.example.cafe_con_huellas.mapper.DonationMapper;
import com.example.cafe_con_huellas.model.entity.Donation;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestión de donaciones económicas.
 * <p>
 * Permite registrar nuevas aportaciones, consultar el historial
 * y obtener estadísticas de donaciones por usuario o globales.
 * </p>
 */
@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;


    /**
     * Obtiene el listado completo de todas las donaciones registradas.
     * Requiere rol ADMIN.
     *
     * @return lista de {@link DonationDTO} con todos los registros
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DonationDTO> getAllDonations() {
        return donationService.findAll();
    }

    /**
     * Obtiene el detalle de una donación concreta por su identificador.
     * Requiere rol ADMIN.
     *
     * @param id identificador único de la donación
     * @return {@link DonationDTO} con los datos de la donación
     * @throws ResourceNotFoundException si no existe donación con ese ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DonationDTO getDonationById(@PathVariable Long id) {
        return donationService.findById(id);
    }

    /**
     * Elimina una donación del sistema.
     * Requiere rol ADMIN.
     *
     * @param id identificador de la donación a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDonation(@PathVariable Long id) {
        donationService.deleteById(id);
    }


    /**
     * Registra una nueva donación en el sistema.
     * <p>
     * La fecha de la donación se asigna automáticamente en el servicio.
     * </p>
     *
     * @param donationDTO datos de la donación a registrar
     * @return {@link DonationDTO} con el registro persistido
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDTO createDonation(@Valid @RequestBody DonationDTO donationDTO) {
        // Pasamos el DTO directamente al service
        return donationService.save(donationDTO);
    }

    /**
     * Obtiene todas las donaciones realizadas por un usuario específico.
     *
     * @param userId identificador del usuario
     * @return lista de {@link DonationDTO} del usuario indicado
     */
    @GetMapping("/user/{userId}")
    public List<DonationDTO> getDonationsByUser(@PathVariable Long userId) {
        return donationService.findByUserId(userId);
    }

    /**
     * Devuelve las donaciones del usuario autenticado.
     * <p>
     * No requiere rol específico: cualquier usuario con un token válido
     * puede consultar sus propias donaciones. El backend identifica al usuario
     * a partir del email almacenado en el JWT (subject), nunca desde
     * un parámetro enviado por el cliente.
     * </p>
     *
     * @return lista de {@link DonationDTO} del usuario autenticado
     */
    @GetMapping("/me")
    public List<DonationDTO> getMyDonations() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return donationService.findByUserEmail(email);
    }

    /**
     * Filtra las donaciones por categoría.
     * Requiere rol ADMIN.
     *
     * @param category nombre de la categoría por la que filtrar
     * @return lista de {@link DonationDTO} de la categoría indicada
     */
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DonationDTO> getDonationsByCategory(@PathVariable String category) {
        return donationService.findByCategory(category);
    }

    /**
     * Calcula el importe total donado por un usuario específico.
     *
     * @param userId identificador del usuario
     * @return importe total acumulado en sus donaciones
     */
    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalByUser(@PathVariable Long userId) {
        return donationService.getTotalAmountByUser(userId);
    }

    /**
     * Calcula el importe total de todas las donaciones recibidas.
     * Requiere rol ADMIN.
     *
     * @return importe total global de donaciones
     */
    @GetMapping("/total")
    @PreAuthorize("hasRole('ADMIN')")
    public BigDecimal getTotalGlobal() {
        return donationService.getTotalDonationsAmount();
    }


}