package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetImageMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetImage;
import com.example.cafe_con_huellas.repository.PetImageRepository;
import com.example.cafe_con_huellas.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetImageServiceTest {

    @Mock
    private PetImageRepository petImageRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetImageMapper petImageMapper;

    @InjectMocks
    private PetImageService petImageService;

    private Pet testPet;
    private PetImage testPetImage;
    private PetImageDTO testPetImageDTO;

    @BeforeEach
    void setUp() {
        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("Firu");

        testPetImage = new PetImage();
        testPetImage.setId(1L);
        testPetImage.setPet(testPet);
        testPetImage.setImageUrl("https://images.com/firu.jpg");

        testPetImageDTO = new PetImageDTO();
        testPetImageDTO.setId(1L);
        testPetImageDTO.setPetId(1L);
        testPetImageDTO.setImageUrl("https://images.com/firu.jpg");
    }

    @Test
    @DisplayName("Debe devolver todas las imágenes correctamente")
    void shouldFindAllImages() {
        when(petImageRepository.findAll()).thenReturn(List.of(testPetImage));
        when(petImageMapper.toDto(testPetImage)).thenReturn(testPetImageDTO);

        List<PetImageDTO> result = petImageService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageUrl()).isEqualTo("https://images.com/firu.jpg");
    }

    @Test
    @DisplayName("Debe encontrar una imagen por su ID")
    void shouldFindImageById() {
        when(petImageRepository.findById(1L)).thenReturn(Optional.of(testPetImage));
        when(petImageMapper.toDto(testPetImage)).thenReturn(testPetImageDTO);

        PetImageDTO result = petImageService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://images.com/firu.jpg");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la imagen no existe")
    void shouldThrowExceptionWhenImageNotFound() {
        when(petImageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> petImageService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar una imagen vinculada a una mascota correctamente")
    void shouldSaveImage() {
        when(petImageMapper.toEntity(testPetImageDTO)).thenReturn(testPetImage);
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petImageRepository.save(any(PetImage.class))).thenReturn(testPetImage);
        when(petImageMapper.toDto(testPetImage)).thenReturn(testPetImageDTO);

        PetImageDTO result = petImageService.save(testPetImageDTO);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://images.com/firu.jpg");
        verify(petImageRepository, times(1)).save(any(PetImage.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota no existe al guardar imagen")
    void shouldThrowExceptionWhenPetNotFoundOnSave() {
        when(petImageMapper.toEntity(testPetImageDTO)).thenReturn(testPetImage);
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> petImageService.save(testPetImageDTO));

        verify(petImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar una imagen existente correctamente")
    void shouldDeleteImageById() {
        when(petImageRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> petImageService.deleteById(1L));

        verify(petImageRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar una imagen que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentImage() {
        when(petImageRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> petImageService.deleteById(99L));

        verify(petImageRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe devolver todas las imágenes de una mascota concreta")
    void shouldFindImagesByPetId() {
        when(petImageRepository.findByPetId(1L)).thenReturn(List.of(testPetImage));
        when(petImageMapper.toDto(testPetImage)).thenReturn(testPetImageDTO);

        List<PetImageDTO> result = petImageService.findByPetId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe eliminar todas las imágenes de una mascota si las tiene")
    void shouldDeleteImagesByPetId() {
        when(petImageRepository.findByPetId(1L)).thenReturn(List.of(testPetImage));

        assertDoesNotThrow(() -> petImageService.deleteByPetId(1L));

        verify(petImageRepository, times(1)).deleteByPetId(1L);
    }

    @Test
    @DisplayName("No debe llamar a deleteByPetId si la mascota no tiene imágenes")
    void shouldNotDeleteWhenNoImagesFound() {
        when(petImageRepository.findByPetId(99L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> petImageService.deleteByPetId(99L));

        // No debe llamar al delete si no hay imágenes
        verify(petImageRepository, never()).deleteByPetId(any());
    }
}