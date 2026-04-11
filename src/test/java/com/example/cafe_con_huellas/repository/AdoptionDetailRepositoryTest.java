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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AdoptionDetailRepositoryTest {

    @Autowired
    private AdoptionDetailRepository adoptionDetailRepository;

    @Autowired
    private UserPetRelationshipRepository userPetRelationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    private UserPetRelationship testRelationship;

    @BeforeEach
    void setUp() {
        adoptionDetailRepository.deleteAll();
        userPetRelationshipRepository.deleteAll();
        petRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = User.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("password123")
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        Pet testPet = Pet.builder()
                .name("Firu")
                .description("Perro juguetón")
                .breed("Labrador")
                .category(PetCategory.PERRO)
                .age(3)
                .weight(BigDecimal.valueOf(25.0))
                .neutered(true)
                .isPpp(false)
                .build();
        testPet = petRepository.save(testPet);

        testRelationship = UserPetRelationship.builder()
                .user(testUser)
                .pet(testPet)
                .relationshipType(RelationshipType.ADOPCION)
                .startDate(LocalDate.now().minusMonths(1))
                .active(true)
                .build();
        testRelationship = userPetRelationshipRepository.save(testRelationship);

        adoptionDetailRepository.save(AdoptionDetail.builder()
                .relationship(testRelationship)
                .adoptionDate(LocalDate.now().minusMonths(1))
                .place("Refugio Jerez")
                .conditions("Sin condiciones especiales")
                .notes("Adopción completada correctamente")
                .build());
    }

    @Test
    @DisplayName("Debe encontrar el detalle de adopción por id de relación")
    void shouldFindByRelationshipId() {
        AdoptionDetail result = adoptionDetailRepository.findByRelationshipId(testRelationship.getId());

        assertThat(result).isNotNull();
        assertThat(result.getRelationship().getId()).isEqualTo(testRelationship.getId());
    }

    @Test
    @DisplayName("Debe devolver null si no existe detalle para esa relación")
    void shouldReturnNullWhenRelationshipNotFound() {
        AdoptionDetail result = adoptionDetailRepository.findByRelationshipId(99L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Debe devolver true si ya existe detalle para esa relación")
    void shouldReturnTrueWhenRelationshipExists() {
        boolean exists = adoptionDetailRepository.existsByRelationshipId(testRelationship.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si no existe detalle para esa relación")
    void shouldReturnFalseWhenRelationshipNotExists() {
        boolean exists = adoptionDetailRepository.existsByRelationshipId(99L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe guardar un detalle de adopción correctamente")
    void shouldSaveAdoptionDetail() {
        // Creamos una segunda relación para evitar el conflicto de unicidad OneToOne
        UserPetRelationship secondRelationship = userPetRelationshipRepository.save(
                UserPetRelationship.builder()
                        .user(userRepository.findAll().get(0))
                        .pet(petRepository.findAll().get(0))
                        .relationshipType(RelationshipType.ACOGIDA)
                        .startDate(LocalDate.now())
                        .active(true)
                        .build());

        AdoptionDetail detail = AdoptionDetail.builder()
                .relationship(secondRelationship)
                .adoptionDate(LocalDate.now())
                .place("Protectora Sevilla")
                .conditions("Visita de seguimiento en 3 meses")
                .issues("Ninguno")
                .notes("Todo correcto")
                .build();

        AdoptionDetail saved = adoptionDetailRepository.save(detail);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlace()).isEqualTo("Protectora Sevilla");
    }
}