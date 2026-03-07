package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AdoptionFormTokenRepositoryTest {

    @Autowired
    private AdoptionFormTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    private User testUser;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        petRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("password123")
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        testPet = Pet.builder()
                .name("Firu")
                .description("Perro muy juguetón")
                .breed("Labrador")
                .category(PetCategory.PERRO)
                .age(3)
                .weight(BigDecimal.valueOf(25.0))
                .neutered(true)
                .isPpp(false)
                .build();
        testPet = petRepository.save(testPet);

        // Token activo usando setters para evitar conflicto con palabra reservada en Lombok
        AdoptionFormToken activeToken = new AdoptionFormToken();
        activeToken.setToken("token-activo-123");
        activeToken.setUser(testUser);
        activeToken.setPet(testPet);
        tokenRepository.save(activeToken);
    }

    @Test
    @DisplayName("Debe encontrar un token por su valor")
    void shouldFindByToken() {
        Optional<AdoptionFormToken> result = tokenRepository.findByToken("token-activo-123");

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("token-activo-123");
    }

    @Test
    @DisplayName("Debe devolver vacío si el token no existe")
    void shouldReturnEmptyWhenTokenNotFound() {
        Optional<AdoptionFormToken> result = tokenRepository.findByToken("token-inexistente");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe devolver true si existe token activo para ese usuario y mascota")
    void shouldReturnTrueWhenActiveTokenExists() {
        boolean exists = tokenRepository.existsByUserIdAndPetIdAndUsedFalse(
                testUser.getId(), testPet.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si el token fue usado")
    void shouldReturnFalseWhenTokenIsUsed() {
        AdoptionFormToken token = tokenRepository.findByToken("token-activo-123").get();
        token.setUsed(true);
        tokenRepository.save(token);

        boolean exists = tokenRepository.existsByUserIdAndPetIdAndUsedFalse(
                testUser.getId(), testPet.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe devolver false si no existe token para ese usuario y mascota")
    void shouldReturnFalseWhenNoTokenForUserAndPet() {
        boolean exists = tokenRepository.existsByUserIdAndPetIdAndUsedFalse(99L, 99L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe guardar el token con @PrePersist correctamente")
    void shouldSaveTokenWithPrePersistValues() {
        AdoptionFormToken nuevoToken = new AdoptionFormToken();
        nuevoToken.setToken("token-nuevo-456");
        nuevoToken.setUser(testUser);
        nuevoToken.setPet(testPet);

        AdoptionFormToken saved = tokenRepository.save(nuevoToken);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsed()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getExpiresAt()).isNotNull();
        assertThat(saved.getExpiresAt()).isAfter(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Debe eliminar un token correctamente")
    void shouldDeleteToken() {
        AdoptionFormToken token = tokenRepository.findByToken("token-activo-123").get();
        Long id = token.getId();

        tokenRepository.deleteById(id);

        assertThat(tokenRepository.findById(id)).isEmpty();
    }
}