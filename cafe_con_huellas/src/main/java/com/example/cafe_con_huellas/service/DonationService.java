package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.DonationDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.DonationMapper;
import com.example.cafe_con_huellas.model.entity.Donation;
import com.example.cafe_con_huellas.model.entity.DonationCategory;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.DonationRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Servicio encargado de la lógica de negocio relacionada con las donaciones
@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final DonationMapper donationMapper;

    // ---------- CRUD BÁSICO ----------

    // Devuelve todas las donaciones registradas
    public List<DonationDTO> findAll() {
        return donationRepository.findAll().stream()
                .map(donationMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca una donación por su ID
    public DonationDTO findById(Long id) {
        return donationRepository.findById(id)
                .map(donationMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));
    }

    // Registra una nueva donación
    @Transactional
    public DonationDTO save(DonationDTO donationDto) {
        // 1. Convertimos DTO a Entidad (vía Mapper)
        Donation donation = donationMapper.toEntity(donationDto);

        // 2. Lógica para el Usuario (Donación Anónima vs Registrada)
        if (donationDto.getUserId() != null) {
            User user = userRepository.findById(donationDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + donationDto.getUserId()));
            donation.setUser(user);
        } else {
            // Si el userId es null, la relación 'user' en la entidad se queda como null (anónima)
            donation.setUser(null);
        }

        // 3. Guardamos (La fecha y validaciones de negocio ya están en la Entity/DTO)
        Donation savedDonation = donationRepository.save(donation);

        return donationMapper.toDto(savedDonation);
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
    public List<DonationDTO> findByUserId(Long userId) {
        return donationRepository.findByUserId(userId).stream()
                .map(donationMapper::toDto)
                .collect(Collectors.toList());
    }

    // Obtiene donaciones filtradas por categoria
    public List<DonationDTO> findByCategory(String category) {
        // Convertimos el String que viene del Front a Enum para el Repository
        try {
            DonationCategory cat = DonationCategory.valueOf(category.toUpperCase());
            return donationRepository.findByCategory(cat).stream()
                    .map(donationMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new com.example.cafe_con_huellas.exception.BadRequestException("Categoría no válida: " + category);
        }
    }


    // Calcula el total donado por un usuario
    public BigDecimal getTotalAmountByUser(Long userId) {
        BigDecimal total = donationRepository.sumAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Calcula el total acumulado de todas las donaciones
    public BigDecimal getTotalDonationsAmount() {
        BigDecimal total = donationRepository.sumTotalAmount();
        return total != null ? total : BigDecimal.ZERO;
    }


    // Actualizar donación
    @Transactional
    public DonationDTO updateDonation(DonationDTO donationDto) {
        if (donationDto.getId() != null) {
            if (!donationRepository.existsById(donationDto.getId())) {
                throw new ResourceNotFoundException("No se puede actualizar. Donación no encontrada con ID: " + donationDto.getId());
            }
        }

        // Convertimos DTO a Entidad
        Donation donation = donationMapper.toEntity(donationDto);

        // Lógica para el Usuario (Donación Anónima vs Registrada)
        if (donationDto.getUserId() != null) {
            User user = userRepository.findById(donationDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + donationDto.getUserId()));
            donation.setUser(user);
        } else {
            donation.setUser(null);
        }

        // Guardamos la entidad
        Donation savedDonation = donationRepository.save(donation);

        // Retornamos el DTO (Siguiendo el flujo: Entity -> DTO)
        return donationMapper.toDto(savedDonation);
    }



}
