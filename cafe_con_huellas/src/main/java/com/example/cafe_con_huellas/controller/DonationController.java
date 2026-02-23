package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.DonationDTO;
import com.example.cafe_con_huellas.mapper.DonationMapper;
import com.example.cafe_con_huellas.model.entity.Donation;
import com.example.cafe_con_huellas.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* Controlador REST para la gestión de donaciones económicas.
 * Permite registrar nuevas aportaciones y consultar el historial.
 */
@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final DonationMapper donationMapper;

    // Devuelve el listado completo de todas las donaciones registradas
    @GetMapping
    public List<DonationDTO> getAllDonations() {
        return donationService.findAll().stream()
                .map(donationMapper::toDto)
                .toList();
    }

    /* Registra una nueva donación en el sistema.
     * El servicio se encarga de asignar la fecha actual automáticamente.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDTO createDonation(@RequestBody DonationDTO donationDTO) {
        Donation donation = donationMapper.toEntity(donationDTO);
        return donationMapper.toDto(donationService.save(donation));
    }

    // Obtiene todas las donaciones realizadas por un usuario específico
    @GetMapping("/user/{userId}")
    public List<DonationDTO> getDonationsByUser(@PathVariable Long userId) {
        return donationService.findByUserId(userId).stream()
                .map(donationMapper::toDto)
                .toList();
    }
}