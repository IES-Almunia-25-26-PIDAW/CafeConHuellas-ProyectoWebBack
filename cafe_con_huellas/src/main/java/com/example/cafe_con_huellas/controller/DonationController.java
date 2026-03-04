package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.DonationDTO;
import com.example.cafe_con_huellas.mapper.DonationMapper;
import com.example.cafe_con_huellas.model.entity.Donation;
import com.example.cafe_con_huellas.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/* Controlador REST para la gestión de donaciones económicas.
 * Permite registrar nuevas aportaciones y consultar el historial.
 */
@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    // Devuelve el listado completo de todas las donaciones registradas
    @GetMapping
    public List<DonationDTO> getAllDonations() {
        return donationService.findAll();
    }

    // Obtener una donación por ID
    @GetMapping("/{id}")
    public DonationDTO getDonationById(@PathVariable Long id) {
        return donationService.findById(id);
    }

    // Eliminar una donación
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDonation(@PathVariable Long id) {
        donationService.deleteById(id);
    }


    /* Registra una nueva donación en el sistema.
     * El servicio se encarga de asignar la fecha actual automáticamente.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDTO createDonation(@Valid @RequestBody DonationDTO donationDTO) {
        // Pasamos el DTO directamente al service
        return donationService.save(donationDTO);
    }

    // Obtiene todas las donaciones realizadas por un usuario específico
    @GetMapping("/user/{userId}")
    public List<DonationDTO> getDonationsByUser(@PathVariable Long userId) {
        return donationService.findByUserId(userId);
    }

    // Obtener donaciones por categoría
    @GetMapping("/category/{category}")
    public List<DonationDTO> getDonationsByCategory(@PathVariable String category) {
        return donationService.findByCategory(category);
    }

    // Estadísticas: Total por usuario
    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalByUser(@PathVariable Long userId) {
        return donationService.getTotalAmountByUser(userId);
    }

    // Estadísticas: Total global
    @GetMapping("/total")
    public BigDecimal getTotalGlobal() {
        return donationService.getTotalDonationsAmount();
    }


}