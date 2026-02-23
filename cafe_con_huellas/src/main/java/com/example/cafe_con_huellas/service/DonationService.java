package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.Donation;
import com.example.cafe_con_huellas.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// Servicio encargado de la lógica de negocio relacionada con las donaciones
@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

    // ---------- CRUD BÁSICO ----------

    // Devuelve todas las donaciones registradas
    public List<Donation> findAll() {
        return donationRepository.findAll();
    }

    // Busca una donación por su ID
    public Donation findById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));
    }

    // Registra una nueva donación
    public Donation save(Donation donation) {
        // Validación: No permitimos donaciones sin importe o negativas
        if (donation.getAmount() == null || donation.getAmount().doubleValue() <= 0) {
            throw new BadRequestException("El importe de la donación debe ser mayor que cero.");
        }

        if (donation.getDate() == null) {
            donation.setDate(LocalDateTime.now());
        }
        return donationRepository.save(donation);
    }

    // Elimina una donación por su ID
    public void deleteById(Long id) {
        if (!donationRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Donación no encontrada");
        }
        donationRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Obtiene todas las donaciones realizadas por un usuario concreto
    public List<Donation> findByUserId(Long userId) {
        return donationRepository.findByUserId(userId);
    }

    // Obtiene donaciones filtradas por tipo
    public List<Donation> findByType(String type) {
        return donationRepository.findByType(type);
    }

    // Calcula el total donado por un usuario
    public Double getTotalAmountByUser(Long userId) {
        return donationRepository.sumAmountByUserId(userId);
    }

    // Calcula el total acumulado de todas las donaciones
    public Double getTotalDonationsAmount() {
        return donationRepository.sumTotalAmount();
    }
}
