package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
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
class PetRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();

        Pet perro = Pet.builder()
                .name("Firu")
                .description("Perro muy juguetón y cariñoso")
                .breed("Labrador")
                .category(PetCategory.PERRO)
                .age(3)
                .weight(BigDecimal.valueOf(25.0))
                .neutered(true)
                .isPpp(false)
                .build();

        Pet gato = Pet.builder()
                .name("Misu")
                .description("Gato tranquilo y hogareño")
                .breed("Europeo")
                .category(PetCategory.GATO)
                .age(2)
                .weight(BigDecimal.valueOf(4.5))
                .neutered(false)
                .isPpp(false)
                .build();

        Pet ppp = Pet.builder()
                .name("Rex")
                .description("Perro potencialmente peligroso bien entrenado")
                .breed("Rottweiler")
                .category(PetCategory.PERRO)
                .age(5)
                .weight(BigDecimal.valueOf(40.0))
                .neutered(true)
                .isPpp(true)
                .build();

        petRepository.saveAll(List.of(perro, gato, ppp));
    }

    @Test
    @DisplayName("Debe devolver mascotas filtradas por esterilización")
    void shouldFindByNeutered() {
        List<Pet> neutered = petRepository.findByNeutered(true);
        List<Pet> notNeutered = petRepository.findByNeutered(false);

        assertThat(neutered).hasSize(2);
        assertThat(notNeutered).hasSize(1);
        assertThat(notNeutered.get(0).getName()).isEqualTo("Misu");
    }

    @Test
    @DisplayName("Debe devolver mascotas filtradas por categoría")
    void shouldFindByCategory() {
        List<Pet> perros = petRepository.findByCategory(PetCategory.PERRO);
        List<Pet> gatos = petRepository.findByCategory(PetCategory.GATO);

        assertThat(perros).hasSize(2);
        assertThat(gatos).hasSize(1);
        assertThat(gatos.get(0).getName()).isEqualTo("Misu");
    }

    @Test
    @DisplayName("Debe devolver mascotas PPP correctamente")
    void shouldFindByIsPpp() {
        List<Pet> pppPets = petRepository.findByIsPpp(true);
        List<Pet> nonPppPets = petRepository.findByIsPpp(false);

        assertThat(pppPets).hasSize(1);
        assertThat(pppPets.get(0).getName()).isEqualTo("Rex");
        assertThat(nonPppPets).hasSize(2);
    }

    @Test
    @DisplayName("Debe devolver mascotas con edad menor o igual a la indicada")
    void shouldFindByAgeLessThanEqual() {
        List<Pet> result = petRepository.findByAgeLessThanEqual(3);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Pet::getName)
                .containsExactlyInAnyOrder("Firu", "Misu");
    }

    @Test
    @DisplayName("Debe buscar mascotas por nombre ignorando mayúsculas")
    void shouldFindByNameContainingIgnoreCase() {
        List<Pet> result = petRepository
                .findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase("firu", "firu");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Firu");
    }

    @Test
    @DisplayName("Debe buscar mascotas por raza ignorando mayúsculas")
    void shouldFindByBreedContainingIgnoreCase() {
        List<Pet> result = petRepository
                .findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase("labrador", "labrador");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBreed()).isEqualTo("Labrador");
    }

    @Test
    @DisplayName("Debe devolver lista vacía si no hay coincidencias en la búsqueda")
    void shouldReturnEmptyWhenNoMatch() {
        List<Pet> result = petRepository
                .findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase("xyz", "xyz");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe guardar y recuperar una mascota correctamente")
    void shouldSaveAndFindPet() {
        Pet nueva = Pet.builder()
                .name("Luna")
                .description("Perrita muy dulce")
                .breed("Mestiza")
                .category(PetCategory.PERRO)
                .age(1)
                .weight(BigDecimal.valueOf(8.0))
                .neutered(false)
                .isPpp(false)
                .build();

        Pet saved = petRepository.save(nueva);

        assertThat(saved.getId()).isNotNull();
        assertThat(petRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Debe eliminar una mascota correctamente")
    void shouldDeletePet() {
        Pet pet = petRepository.findByCategory(PetCategory.GATO).get(0);
        Long id = pet.getId();

        petRepository.deleteById(id);

        assertThat(petRepository.findById(id)).isEmpty();
    }
}