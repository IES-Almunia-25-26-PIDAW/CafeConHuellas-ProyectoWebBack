package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import com.example.cafe_con_huellas.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetService petService;

    private Pet testPet;
    private PetDetailDTO testPetDetailDTO;

    @BeforeEach
    void setUp() {
        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("Firu");
        testPet.setBreed("Labrador");
        testPet.setCategory(PetCategory.PERRO);

        testPetDetailDTO = new PetDetailDTO();
        testPetDetailDTO.setId(1L);
        testPetDetailDTO.setName("Firu");
        testPetDetailDTO.setBreed("Labrador");
        testPetDetailDTO.setCategory("PERRO");
    }

    @Test
    @DisplayName("Debe devolver todas las mascotas correctamente")
    void shouldFindAllPets() {
        when(petRepository.findAll()).thenReturn(List.of(testPet));
        when(petMapper.toDetailDto(testPet)).thenReturn(testPetDetailDTO);

        List<PetDetailDTO> result = petService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Firu");
    }

    @Test
    @DisplayName("Debe encontrar una mascota por su ID")
    void shouldFindPetById() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petMapper.toDetailDto(testPet)).thenReturn(testPetDetailDTO);

        PetDetailDTO result = petService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Firu");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la mascota no existe")
    void shouldThrowExceptionWhenPetNotFound() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar una mascota correctamente")
    void shouldSavePet() {
        when(petMapper.toEntity(testPetDetailDTO)).thenReturn(testPet);
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);
        when(petMapper.toDetailDto(testPet)).thenReturn(testPetDetailDTO);

        PetDetailDTO result = petService.save(testPetDetailDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Firu");
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe filtrar mascotas por categoría válida")
    void shouldFindPetsByCategory() {
        when(petRepository.findByCategory(PetCategory.PERRO)).thenReturn(List.of(testPet));
        when(petMapper.toDetailDto(testPet)).thenReturn(testPetDetailDTO);

        List<PetDetailDTO> result = petService.findByCategory("PERRO");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("PERRO");
    }

    @Test
    @DisplayName("Debe lanzar excepción con una categoría no válida")
    void shouldThrowExceptionForInvalidCategory() {
        assertThrows(BadRequestException.class, () -> petService.findByCategory("DINOSAURIO"));
    }

    @Test
    @DisplayName("Debe buscar mascotas por nombre o raza")
    void shouldSearchPetsByText() {
        when(petRepository.findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase("Firu", "Firu"))
                .thenReturn(List.of(testPet));
        when(petMapper.toDetailDto(testPet)).thenReturn(testPetDetailDTO);

        List<PetDetailDTO> result = petService.search("Firu");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Firu");
    }

    @Test
    @DisplayName("Debe eliminar una mascota existente correctamente")
    void shouldDeletePetById() {
        when(petRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> petService.deleteById(1L));

        verify(petRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar una mascota que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentPet() {
        when(petRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> petService.deleteById(99L));

        verify(petRepository, never()).deleteById(any());
    }
}