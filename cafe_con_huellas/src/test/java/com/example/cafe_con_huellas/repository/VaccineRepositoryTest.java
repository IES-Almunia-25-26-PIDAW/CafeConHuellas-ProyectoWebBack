package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Vaccine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VaccineRepositoryTest {

    @Autowired
    private VaccineRepository vaccineRepository;

    @BeforeEach
    void setUp() {
        vaccineRepository.deleteAll();

        vaccineRepository.save(Vaccine.builder()
                .name("Rabia")
                .description("Vacuna antirrábica obligatoria")
                .build());

        vaccineRepository.save(Vaccine.builder()
                .name("Moquillo")
                .description("Protege contra el virus del moquillo")
                .build());
    }

    @Test
    @DisplayName("Debe encontrar una vacuna por nombre")
    void shouldFindByName() {
        Optional<Vaccine> result = vaccineRepository.findByName("Rabia");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Rabia");
    }

    @Test
    @DisplayName("Debe devolver vacío si la vacuna no existe")
    void shouldReturnEmptyWhenNameNotFound() {
        Optional<Vaccine> result = vaccineRepository.findByName("Inexistente");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe devolver true si la vacuna ya existe por nombre")
    void shouldReturnTrueWhenNameExists() {
        boolean exists = vaccineRepository.existsByName("Moquillo");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si la vacuna no existe por nombre")
    void shouldReturnFalseWhenNameNotExists() {
        boolean exists = vaccineRepository.existsByName("Inexistente");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe guardar una vacuna correctamente")
    void shouldSaveVaccine() {
        Vaccine vaccine = Vaccine.builder()
                .name("Parvovirus")
                .description("Vacuna contra el parvovirus canino")
                .build();

        Vaccine saved = vaccineRepository.save(vaccine);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Parvovirus");
    }
}