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
class UserPetFavoriteRepositoryTest {

    @Autowired
    private UserPetFavoriteRepository userPetFavoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    private User testUser;
    private Pet testPet1;
    private Pet testPet2;

    @BeforeEach
    void setUp() {
        userPetFavoriteRepository.deleteAll();
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

        testPet1 = Pet.builder()
                .name("Firu")
                .description("Perro juguetón")
                .breed("Labrador")
                .category(PetCategory.PERRO)
                .age(3)
                .weight(BigDecimal.valueOf(25.0))
                .neutered(true)
                .isPpp(false)
                .build();
        testPet1 = petRepository.save(testPet1);

        testPet2 = Pet.builder()
                .name("Misi")
                .description("Gato tranquilo")
                .breed("Europeo")
                .category(PetCategory.GATO)
                .age(2)
                .weight(BigDecimal.valueOf(4.0))
                .neutered(true)
                .isPpp(false)
                .build();
        testPet2 = petRepository.save(testPet2);

        userPetFavoriteRepository.save(UserPetFavorite.builder()
                .user(testUser)
                .pet(testPet1)
                .build());
    }

    @Test
    @DisplayName("Debe encontrar favoritos por usuario")
    void shouldFindByUserId() {
        List<UserPetFavorite> result = userPetFavoriteRepository.findByUserId(testUser.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPet().getId()).isEqualTo(testPet1.getId());
    }

    @Test
    @DisplayName("Debe devolver true si la mascota ya está en favoritos")
    void shouldReturnTrueWhenFavoriteExists() {
        boolean exists = userPetFavoriteRepository.existsByUserIdAndPetId(
                testUser.getId(), testPet1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si la mascota no está en favoritos")
    void shouldReturnFalseWhenFavoriteNotExists() {
        boolean exists = userPetFavoriteRepository.existsByUserIdAndPetId(
                testUser.getId(), testPet2.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe eliminar un favorito por usuario y mascota")
    void shouldDeleteByUserIdAndPetId() {
        userPetFavoriteRepository.deleteByUserIdAndPetId(testUser.getId(), testPet1.getId());

        boolean exists = userPetFavoriteRepository.existsByUserIdAndPetId(
                testUser.getId(), testPet1.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe devolver lista vacía si el usuario no tiene favoritos")
    void shouldReturnEmptyWhenNoFavorites() {
        List<UserPetFavorite> result = userPetFavoriteRepository.findByUserId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe guardar un nuevo favorito correctamente")
    void shouldSaveFavorite() {
        UserPetFavorite newFavorite = UserPetFavorite.builder()
                .user(testUser)
                .pet(testPet2)
                .build();

        UserPetFavorite saved = userPetFavoriteRepository.save(newFavorite);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPet().getId()).isEqualTo(testPet2.getId());
    }
}