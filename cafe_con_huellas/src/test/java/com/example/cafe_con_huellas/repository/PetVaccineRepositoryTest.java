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
class PetVaccineRepositoryTest {

    @Autowired
    private PetVaccineRepository petVaccineRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    private Pet testPet;
    private Vaccine testVaccine;

    @BeforeEach
    void setUp() {
        petVaccineRepository.deleteAll();
        petRepository.deleteAll();
        vaccineRepository.deleteAll();

        testPet = Pet.builder()
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

        testVaccine = Vaccine.builder()
                .name("Rabia")
                .description("Vacuna antirrábica")
                .build();
        testVaccine = vaccineRepository.save(testVaccine);

        // Vacuna con próxima dosis en el futuro
        petVaccineRepository.save(PetVaccine.builder()
                .pet(testPet)
                .vaccine(testVaccine)
                .dateAdministered(LocalDate.now().minusMonths(6))
                .nextDoseDate(LocalDate.now().plusMonths(6))
                .notes("Sin reacciones")
                .build());

        // Vacuna con próxima dosis vencida (en el pasado)
        petVaccineRepository.save(PetVaccine.builder()
                .pet(testPet)
                .vaccine(testVaccine)
                .dateAdministered(LocalDate.now().minusYears(1))
                .nextDoseDate(LocalDate.now().minusMonths(1))
                .build());
    }

    @Test
    @DisplayName("Debe encontrar vacunas por mascota")
    void shouldFindByPetId() {
        List<PetVaccine> result = petVaccineRepository.findByPetId(testPet.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(pv -> pv.getPet().getId().equals(testPet.getId()));
    }

    @Test
    @DisplayName("Debe encontrar vacunas por tipo de vacuna")
    void shouldFindByVaccineId() {
        List<PetVaccine> result = petVaccineRepository.findByVaccineId(testVaccine.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(pv -> pv.getVaccine().getId().equals(testVaccine.getId()));
    }

    @Test
    @DisplayName("Debe encontrar vacunas con próxima dosis en el futuro")
    void shouldFindByNextDoseDateAfter() {
        List<PetVaccine> result = petVaccineRepository.findByNextDoseDateAfter(LocalDate.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNextDoseDate()).isAfter(LocalDate.now());
    }

    @Test
    @DisplayName("Debe encontrar vacunas con próxima dosis vencida")
    void shouldFindByNextDoseDateBefore() {
        List<PetVaccine> result = petVaccineRepository.findByNextDoseDateBefore(LocalDate.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNextDoseDate()).isBefore(LocalDate.now());
    }

    @Test
    @DisplayName("Debe devolver lista vacía si la mascota no tiene vacunas")
    void shouldReturnEmptyWhenPetHasNoVaccines() {
        List<PetVaccine> result = petVaccineRepository.findByPetId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe guardar una vacuna aplicada correctamente")
    void shouldSavePetVaccine() {
        PetVaccine petVaccine = PetVaccine.builder()
                .pet(testPet)
                .vaccine(testVaccine)
                .dateAdministered(LocalDate.now())
                .nextDoseDate(LocalDate.now().plusYears(1))
                .notes("Primera dosis")
                .build();

        PetVaccine saved = petVaccineRepository.save(petVaccine);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNotes()).isEqualTo("Primera dosis");
    }
}