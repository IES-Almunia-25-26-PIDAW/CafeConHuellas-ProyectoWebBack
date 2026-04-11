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

/**
 * Servicio encargado de la lógica de negocio relacionada con las donaciones.
 * <p>
 * Soporta tanto donaciones anónimas (sin usuario asociado) como donaciones
 * de usuarios registrados, y ofrece estadísticas de totales.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final DonationMapper donationMapper;

    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todas las donaciones registradas convertidas a DTO.
     *
     * @return lista de {@link DonationDTO} con todos los registros
     */
    public List<DonationDTO> findAll() {
        return donationRepository.findAll().stream()
                .map(donationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca una donación concreta por su identificador.
     *
     * @param id identificador único de la donación
     * @return {@link DonationDTO} con los datos de la donación
     * @throws ResourceNotFoundException si no existe la donación con ese ID
     */
    public DonationDTO findById(Long id) {
        return donationRepository.findById(id)
                .map(donationMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));
    }

    /**
     * Registra una nueva donación en el sistema.
     * <p>
     * Si el {@code userId} es {@code null} se trata como donación anónima.
     * La fecha se asigna automáticamente en el momento de la persistencia.
     * </p>
     *
     * @param donationDto datos de la donación a registrar
     * @return {@link DonationDTO} con el registro persistido
     * @throws ResourceNotFoundException si el usuario referenciado no existe
     */
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

    /**
     * Elimina una donación del sistema por su identificador.
     *
     * @param id identificador de la donación a eliminar
     * @throws ResourceNotFoundException si no existe la donación con ese ID
     */
    public void deleteById(Long id) {
        if (!donationRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Donación no encontrada");
        }
        donationRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    /**
     * Obtiene todas las donaciones realizadas por un usuario concreto.
     *
     * @param userId identificador del usuario
     * @return lista de {@link DonationDTO} del usuario indicado
     */
    public List<DonationDTO> findByUserId(Long userId) {
        return donationRepository.findByUserId(userId).stream()
                .map(donationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Filtra las donaciones por categoría.
     * <p>
     * Convierte el {@code String} recibido del frontend al enum {@link DonationCategory}.
     * </p>
     *
     * @param category nombre de la categoría en texto (insensible a mayúsculas)
     * @return lista de {@link DonationDTO} de la categoría indicada
     * @throws BadRequestException si la categoría no es válida
     */
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


    /**
     * Calcula el importe total donado por un usuario específico.
     *
     * @param userId identificador del usuario
     * @return importe total acumulado, o {@code BigDecimal.ZERO} si no hay donaciones
     */
    public BigDecimal getTotalAmountByUser(Long userId) {
        BigDecimal total = donationRepository.sumAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Calcula el importe total acumulado de todas las donaciones del sistema.
     *
     * @return importe total global, o {@code BigDecimal.ZERO} si no hay donaciones
     */
    public BigDecimal getTotalDonationsAmount() {
        BigDecimal total = donationRepository.sumTotalAmount();
        return total != null ? total : BigDecimal.ZERO;
    }


    /**
     * Actualiza los datos de una donación existente.
     *
     * @param donationDto datos actualizados de la donación, debe incluir un ID válido
     * @return {@link DonationDTO} con el registro actualizado
     * @throws ResourceNotFoundException si no existe la donación con ese ID
     */
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
