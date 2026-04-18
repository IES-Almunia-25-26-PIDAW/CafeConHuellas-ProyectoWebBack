package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DonationRepositoryTest {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        donationRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("password123")
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        // Donación monetaria del usuario
        donationRepository.save(Donation.builder()
                .user(testUser)
                .category(DonationCategory.MONETARIA)
                .method(DonationMethod.BIZUM)
                .amount(BigDecimal.valueOf(50.00))
                .notes("Donación mensual")
                .build());

        // Donación de alimentos del usuario
        donationRepository.save(Donation.builder()
                .user(testUser)
                .category(DonationCategory.ALIMENTACION)
                .method(DonationMethod.EFECTIVO)
                .amount(BigDecimal.valueOf(20.00))
                .build());

        // Donación anónima (sin usuario)
        donationRepository.save(Donation.builder()
                .category(DonationCategory.MONETARIA)
                .method(DonationMethod.TRANSFERENCIA)
                .amount(BigDecimal.valueOf(100.00))
                .build());
    }

    @Test
    @DisplayName("Debe encontrar donaciones por usuario")
    void shouldFindByUserId() {
        List<Donation> result = donationRepository.findByUserId(testUser.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getUser().getId().equals(testUser.getId()));
    }

    @Test
    @DisplayName("Debe encontrar donaciones por email de usuario")
    void shouldFindByUserEmail() {
        List<Donation> result = donationRepository.findByUserEmail("ana@test.com");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getUser().getEmail().equals("ana@test.com"));
    }

    @Test
    @DisplayName("Debe devolver lista vacía si el email no tiene donaciones")
    void shouldReturnEmptyWhenEmailHasNoDonations() {
        List<Donation> result = donationRepository.findByUserEmail("noexiste@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe encontrar donaciones por categoría")
    void shouldFindByCategory() {
        List<Donation> result = donationRepository.findByCategory(DonationCategory.MONETARIA);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getCategory() == DonationCategory.MONETARIA);
    }

    @Test
    @DisplayName("Debe encontrar donaciones por método")
    void shouldFindByMethod() {
        List<Donation> result = donationRepository.findByMethod(DonationMethod.BIZUM);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMethod()).isEqualTo(DonationMethod.BIZUM);
    }

    @Test
    @DisplayName("Debe sumar el total donado por un usuario")
    void shouldSumAmountByUserId() {
        BigDecimal total = donationRepository.sumAmountByUserId(testUser.getId());

        assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(70.00));
    }

    @Test
    @DisplayName("Debe sumar el total de todas las donaciones")
    void shouldSumTotalAmount() {
        BigDecimal total = donationRepository.sumTotalAmount();

        assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(170.00));
    }

    @Test
    @DisplayName("Debe encontrar donaciones anónimas")
    void shouldFindByUserIsNull() {
        List<Donation> result = donationRepository.findByUserIsNull();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser()).isNull();
    }

    @Test
    @DisplayName("Debe devolver lista vacía si el usuario no tiene donaciones")
    void shouldReturnEmptyWhenUserHasNoDonations() {
        List<Donation> result = donationRepository.findByUserId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe guardar donación con @PrePersist asignando fecha")
    void shouldSaveDonationWithDate() {
        Donation donation = Donation.builder()
                .user(testUser)
                .category(DonationCategory.MATERIAL)
                .method(DonationMethod.EFECTIVO)
                .amount(BigDecimal.valueOf(15.00))
                .build();

        Donation saved = donationRepository.save(donation);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDate()).isNotNull();
    }
}