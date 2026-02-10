package com.example.cafe_con_huellas.service;

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
                .orElseThrow(() -> new RuntimeException("Donation not found"));
    }

    // Registra una nueva donación
    public Donation save(Donation donation) {
        // Se asegura de registrar la fecha actual si no viene informada
        if (donation.getDate() == null) {
            donation.setDate(LocalDateTime.now());
        }
        return donationRepository.save(donation);
    }

    // Elimina una donación por su ID
    public void deleteById(Long id) {
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
