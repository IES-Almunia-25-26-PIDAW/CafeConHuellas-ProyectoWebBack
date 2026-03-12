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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AdoptionRequestRepositoryTest {

    @Autowired
    private AdoptionRequestRepository requestRepository;

    @Autowired
    private AdoptionFormTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    private AdoptionFormToken formToken;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        tokenRepository.deleteAll();
        petRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("password123")
                .role(Role.USER)
                .build();
        user = userRepository.save(user);

        Pet pet = Pet.builder()
                .name("Firu")
                .description("Perro muy juguetón")
                .breed("Labrador")
                .category(PetCategory.PERRO)
                .age(3)
                .weight(BigDecimal.valueOf(25.0))
                .neutered(true)
                .isPpp(false)
                .build();
        pet = petRepository.save(pet);

        formToken = new AdoptionFormToken();
        formToken.setToken("token-test-123");
        formToken.setUser(user);
        formToken.setPet(pet);
        formToken = tokenRepository.save(formToken);

        AdoptionRequest request = new AdoptionRequest();
        request.setFormToken(formToken);
        request.setAddress("Calle Mayor 1");
        request.setCity("Jerez");
        request.setHousingType("PISO");
        request.setHasGarden(false);
        request.setHasOtherPets(false);
        request.setHasChildren(false);
        request.setHoursAlonePerDay(4);
        request.setExperienceWithPets(true);
        request.setReasonForAdoption("Quiero darle un hogar");
        request.setAgreesToFollowUp(true);
        requestRepository.save(request);
    }

    @Test
    @DisplayName("findByStatus() devuelve las solicitudes con estado PENDIENTE")
    void shouldFindByStatus() {
        List<AdoptionRequest> result = requestRepository.findByStatus(AdoptionRequestStatus.PENDIENTE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AdoptionRequestStatus.PENDIENTE);
    }

    @Test
    @DisplayName("existsByFormTokenId() devuelve true si ya existe una solicitud para ese token")
    void shouldReturnTrueWhenRequestExistsForToken() {
        boolean exists = requestRepository.existsByFormTokenId(formToken.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByFormTokenId() devuelve false si no existe solicitud para ese token")
    void shouldReturnFalseWhenNoRequestForToken() {
        boolean exists = requestRepository.existsByFormTokenId(999L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByFormTokenId() devuelve la solicitud asociada al token")
    void shouldFindByFormTokenId() {
        Optional<AdoptionRequest> result = requestRepository.findByFormTokenId(formToken.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Jerez");
    }

    @Test
    @DisplayName("findByFormTokenId() devuelve vacío si no existe solicitud para ese token")
    void shouldReturnEmptyWhenTokenHasNoRequest() {
        Optional<AdoptionRequest> result = requestRepository.findByFormTokenId(999L);

        assertThat(result).isEmpty();
    }
}