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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DonationMapper donationMapper;

    @InjectMocks
    private DonationService donationService;

    private Donation testDonation;
    private DonationDTO testDonationDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Ana");
        testUser.setEmail("ana@test.com");

        testDonation = new Donation();
        testDonation.setId(1L);
        testDonation.setAmount(BigDecimal.valueOf(50.00));
        testDonation.setUser(testUser);

        testDonationDTO = new DonationDTO();
        testDonationDTO.setId(1L);
        testDonationDTO.setAmount(BigDecimal.valueOf(50.00));
        testDonationDTO.setUserId(1L);
    }

    @Test
    @DisplayName("Debe devolver todas las donaciones correctamente")
    void shouldFindAllDonations() {
        when(donationRepository.findAll()).thenReturn(List.of(testDonation));
        when(donationMapper.toDto(testDonation)).thenReturn(testDonationDTO);

        List<DonationDTO> result = donationService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(BigDecimal.valueOf(50.00));
    }

    @Test
    @DisplayName("Debe encontrar una donación por su ID")
    void shouldFindDonationById() {
        when(donationRepository.findById(1L)).thenReturn(Optional.of(testDonation));
        when(donationMapper.toDto(testDonation)).thenReturn(testDonationDTO);

        DonationDTO result = donationService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la donación no existe")
    void shouldThrowExceptionWhenDonationNotFound() {
        when(donationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> donationService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar una donación de usuario registrado correctamente")
    void shouldSaveDonationWithUser() {
        when(donationMapper.toEntity(testDonationDTO)).thenReturn(testDonation);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(donationRepository.save(any(Donation.class))).thenReturn(testDonation);
        when(donationMapper.toDto(testDonation)).thenReturn(testDonationDTO);

        DonationDTO result = donationService.save(testDonationDTO);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    @DisplayName("Debe guardar una donación anónima sin usuario")
    void shouldSaveAnonymousDonation() {
        // Donación sin userId (anónima)
        testDonationDTO.setUserId(null);
        when(donationMapper.toEntity(testDonationDTO)).thenReturn(testDonation);
        when(donationRepository.save(any(Donation.class))).thenReturn(testDonation);
        when(donationMapper.toDto(testDonation)).thenReturn(testDonationDTO);

        DonationDTO result = donationService.save(testDonationDTO);

        assertThat(result).isNotNull();
        // Nunca debe buscar usuario si es anónima
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario de la donación no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        when(donationMapper.toEntity(testDonationDTO)).thenReturn(testDonation);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> donationService.save(testDonationDTO));
    }

    @Test
    @DisplayName("Debe eliminar una donación existente correctamente")
    void shouldDeleteDonationById() {
        when(donationRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> donationService.deleteById(1L));

        verify(donationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar una donación que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentDonation() {
        when(donationRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> donationService.deleteById(99L));

        verify(donationRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe filtrar donaciones por categoría válida")
    void shouldFindDonationsByCategory() {
        when(donationRepository.findByCategory(DonationCategory.ALIMENTACION))
                .thenReturn(List.of(testDonation));
        when(donationMapper.toDto(testDonation)).thenReturn(testDonationDTO);

        List<DonationDTO> result = donationService.findByCategory("ALIMENTACION");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción con una categoría de donación no válida")
    void shouldThrowExceptionForInvalidCategory() {
        assertThrows(BadRequestException.class,
                () -> donationService.findByCategory("CATEGORIA_INEXISTENTE"));
    }

    @Test
    @DisplayName("Debe calcular el total donado por un usuario")
    void shouldGetTotalAmountByUser() {
        when(donationRepository.sumAmountByUserId(1L)).thenReturn(BigDecimal.valueOf(150.00));

        BigDecimal total = donationService.getTotalAmountByUser(1L);

        assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    @Test
    @DisplayName("Debe devolver cero si el usuario no tiene donaciones")
    void shouldReturnZeroWhenUserHasNoDonations() {
        when(donationRepository.sumAmountByUserId(99L)).thenReturn(null);

        BigDecimal total = donationService.getTotalAmountByUser(99L);

        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe calcular el total global de donaciones")
    void shouldGetTotalDonationsAmount() {
        when(donationRepository.sumTotalAmount()).thenReturn(BigDecimal.valueOf(500.00));

        BigDecimal total = donationService.getTotalDonationsAmount();

        assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(500.00));
    }

    @Test
    @DisplayName("Debe devolver cero si no hay donaciones en el sistema")
    void shouldReturnZeroWhenNoDonations() {
        when(donationRepository.sumTotalAmount()).thenReturn(null);

        BigDecimal total = donationService.getTotalDonationsAmount();

        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }
}