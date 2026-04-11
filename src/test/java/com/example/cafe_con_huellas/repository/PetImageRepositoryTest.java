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
class PetImageRepositoryTest {

    @Autowired
    private PetImageRepository petImageRepository;

    @Autowired
    private PetRepository petRepository;

    private Pet testPet;
    private Pet otherPet;

    @BeforeEach
    void setUp() {
        petImageRepository.deleteAll();
        petRepository.deleteAll();

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

        otherPet = Pet.builder()
                .name("Misi")
                .description("Gato tranquilo")
                .breed("Europeo")
                .category(PetCategory.GATO)
                .age(2)
                .weight(BigDecimal.valueOf(4.0))
                .neutered(true)
                .isPpp(false)
                .build();
        otherPet = petRepository.save(otherPet);

        petImageRepository.save(PetImage.builder()
                .pet(testPet)
                .imageUrl("https://example.com/firu1.jpg")
                .build());

        petImageRepository.save(PetImage.builder()
                .pet(testPet)
                .imageUrl("https://example.com/firu2.jpg")
                .build());

        petImageRepository.save(PetImage.builder()
                .pet(otherPet)
                .imageUrl("https://example.com/misi1.jpg")
                .build());
    }

    @Test
    @DisplayName("Debe encontrar imágenes por mascota")
    void shouldFindByPetId() {
        List<PetImage> result = petImageRepository.findByPetId(testPet.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(img -> img.getPet().getId().equals(testPet.getId()));
    }

    @Test
    @DisplayName("Debe devolver lista vacía si la mascota no tiene imágenes")
    void shouldReturnEmptyWhenPetHasNoImages() {
        List<PetImage> result = petImageRepository.findByPetId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe eliminar todas las imágenes de una mascota")
    void shouldDeleteByPetId() {
        petImageRepository.deleteByPetId(testPet.getId());

        List<PetImage> result = petImageRepository.findByPetId(testPet.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("No debe eliminar imágenes de otras mascotas al borrar una")
    void shouldNotDeleteOtherPetsImages() {
        petImageRepository.deleteByPetId(testPet.getId());

        List<PetImage> result = petImageRepository.findByPetId(otherPet.getId());
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Debe guardar una imagen correctamente")
    void shouldSavePetImage() {
        PetImage image = PetImage.builder()
                .pet(testPet)
                .imageUrl("https://example.com/firu3.jpg")
                .build();

        PetImage saved = petImageRepository.save(image);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getImageUrl()).isEqualTo("https://example.com/firu3.jpg");
    }
}