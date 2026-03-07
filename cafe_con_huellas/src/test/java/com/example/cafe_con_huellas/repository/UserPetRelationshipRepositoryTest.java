package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserPetRelationshipRepositoryTest {

    @Autowired
    private UserPetRelationshipRepository userPetRelationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    private User testUser;
    private Pet testPet1;
    private Pet testPet2;

    @BeforeEach
    void setUp() {
        userPetRelationshipRepository.deleteAll();
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

        // Relación activa de adopción
        userPetRelationshipRepository.save(UserPetRelationship.builder()
                .user(testUser)
                .pet(testPet1)
                .relationshipType(RelationshipType.ADOPCION)
                .startDate(LocalDate.now().minusMonths(3))
                .active(true)
                .build());

        // Relación finalizada de acogida
        userPetRelationshipRepository.save(UserPetRelationship.builder()
                .user(testUser)
                .pet(testPet2)
                .relationshipType(RelationshipType.ACOGIDA)
                .startDate(LocalDate.now().minusMonths(6))
                .endDate(LocalDate.now().minusMonths(1))
                .active(false)
                .build());
    }

    @Test
    @DisplayName("Debe encontrar relaciones por usuario")
    void shouldFindByUserId() {
        List<UserPetRelationship> result = userPetRelationshipRepository.findByUserId(testUser.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.getUser().getId().equals(testUser.getId()));
    }

    @Test
    @DisplayName("Debe encontrar relaciones por mascota")
    void shouldFindByPetId() {
        List<UserPetRelationship> result = userPetRelationshipRepository.findByPetId(testPet1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPet().getId()).isEqualTo(testPet1.getId());
    }

    @Test
    @DisplayName("Debe encontrar solo relaciones activas")
    void shouldFindByActiveTrue() {
        List<UserPetRelationship> result = userPetRelationshipRepository.findByActiveTrue();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
    }

    @Test
    @DisplayName("Debe devolver lista vacía si el usuario no tiene relaciones")
    void shouldReturnEmptyWhenUserHasNoRelationships() {
        List<UserPetRelationship> result = userPetRelationshipRepository.findByUserId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe devolver lista vacía si la mascota no tiene relaciones")
    void shouldReturnEmptyWhenPetHasNoRelationships() {
        List<UserPetRelationship> result = userPetRelationshipRepository.findByPetId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe guardar una relación correctamente")
    void shouldSaveRelationship() {
        UserPetRelationship relationship = UserPetRelationship.builder()
                .user(testUser)
                .pet(testPet1)
                .relationshipType(RelationshipType.PASEO)
                .startDate(LocalDate.now())
                .active(true)
                .build();

        UserPetRelationship saved = userPetRelationshipRepository.save(relationship);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRelationshipType()).isEqualTo(RelationshipType.PASEO);
    }

    @Test
    @DisplayName("Debe actualizar el estado de una relación a inactiva")
    void shouldUpdateRelationshipToInactive() {
        UserPetRelationship relationship = userPetRelationshipRepository
                .findByUserId(testUser.getId()).stream()
                .filter(UserPetRelationship::getActive)
                .findFirst().get();

        relationship.setActive(false);
        relationship.setEndDate(LocalDate.now());
        userPetRelationshipRepository.save(relationship);

        List<UserPetRelationship> activeResult = userPetRelationshipRepository.findByActiveTrue();
        assertThat(activeResult).isEmpty();
    }
}