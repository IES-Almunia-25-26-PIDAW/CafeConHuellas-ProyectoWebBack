package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.AdoptionDetailMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.repository.AdoptionDetailRepository;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
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
class AdoptionDetailServiceTest {

    @Mock
    private AdoptionDetailRepository adoptionDetailRepository;

    @Mock
    private AdoptionDetailMapper adoptionDetailMapper;

    @Mock
    private UserPetRelationshipRepository relationshipRepository;

    @InjectMocks
    private AdoptionDetailService adoptionDetailService;

    private AdoptionDetail testDetail;
    private AdoptionDetailDTO testDetailDTO;
    private UserPetRelationship testRelationship;

    @BeforeEach
    void setUp() {
        testRelationship = new UserPetRelationship();
        testRelationship.setId(1L);

        testDetail = new AdoptionDetail();
        testDetail.setId(1L);
        testDetail.setPlace("Jerez de la Frontera");
        testDetail.setConditions("Buenas condiciones");
        testDetail.setNotes("Sin novedades");
        testDetail.setRelationship(testRelationship);

        testDetailDTO = new AdoptionDetailDTO();
        testDetailDTO.setId(1L);
        testDetailDTO.setPlace("Jerez de la Frontera");
        testDetailDTO.setConditions("Buenas condiciones");
        testDetailDTO.setNotes("Sin novedades");
        testDetailDTO.setUserPetRelationshipId(1L);
    }

    @Test
    @DisplayName("Debe devolver todos los detalles de adopción correctamente")
    void shouldFindAllDetails() {
        when(adoptionDetailRepository.findAll()).thenReturn(List.of(testDetail));
        when(adoptionDetailMapper.toDto(testDetail)).thenReturn(testDetailDTO);

        List<AdoptionDetailDTO> result = adoptionDetailService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlace()).isEqualTo("Jerez de la Frontera");
    }

    @Test
    @DisplayName("Debe encontrar un detalle por su ID")
    void shouldFindDetailById() {
        when(adoptionDetailRepository.findById(1L)).thenReturn(Optional.of(testDetail));
        when(adoptionDetailMapper.toDto(testDetail)).thenReturn(testDetailDTO);

        AdoptionDetailDTO result = adoptionDetailService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPlace()).isEqualTo("Jerez de la Frontera");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el detalle no existe")
    void shouldThrowExceptionWhenDetailNotFound() {
        when(adoptionDetailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adoptionDetailService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar un nuevo detalle de adopción correctamente")
    void shouldSaveNewDetail() {
        // Sin ID porque es nuevo
        testDetailDTO.setId(null);
        // No existe duplicado previo
        when(adoptionDetailRepository.existsByRelationshipId(1L)).thenReturn(false);
        when(relationshipRepository.findById(1L)).thenReturn(Optional.of(testRelationship));
        when(adoptionDetailMapper.toEntity(testDetailDTO)).thenReturn(testDetail);
        when(adoptionDetailRepository.save(any(AdoptionDetail.class))).thenReturn(testDetail);
        when(adoptionDetailMapper.toDto(testDetail)).thenReturn(testDetailDTO);

        AdoptionDetailDTO result = adoptionDetailService.save(testDetailDTO);

        assertThat(result).isNotNull();
        verify(adoptionDetailRepository, times(1)).save(any(AdoptionDetail.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existen detalles para esa relación")
    void shouldThrowExceptionWhenDuplicateRelationship() {
        testDetailDTO.setId(null);
        // Ya existe un detalle para esta relación
        when(adoptionDetailRepository.existsByRelationshipId(1L)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> adoptionDetailService.save(testDetailDTO));

        verify(adoptionDetailRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la relación no existe al guardar")
    void shouldThrowExceptionWhenRelationshipNotFound() {
        testDetailDTO.setId(null);
        when(adoptionDetailRepository.existsByRelationshipId(1L)).thenReturn(false);
        when(relationshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adoptionDetailService.save(testDetailDTO));

        verify(adoptionDetailRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar un detalle existente correctamente")
    void shouldDeleteDetailById() {
        when(adoptionDetailRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> adoptionDetailService.deleteById(1L));

        verify(adoptionDetailRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un detalle que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentDetail() {
        when(adoptionDetailRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> adoptionDetailService.deleteById(99L));

        verify(adoptionDetailRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe encontrar detalles por ID de relación")
    void shouldFindDetailByRelationshipId() {
        when(adoptionDetailRepository.findByRelationshipId(1L)).thenReturn(testDetail);
        when(adoptionDetailMapper.toDto(testDetail)).thenReturn(testDetailDTO);

        AdoptionDetailDTO result = adoptionDetailService.findByRelationshipId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserPetRelationshipId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no hay detalles para esa relación")
    void shouldThrowExceptionWhenNoDetailsForRelationship() {
        when(adoptionDetailRepository.findByRelationshipId(99L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> adoptionDetailService.findByRelationshipId(99L));
    }

    @Test
    @DisplayName("Debe actualizar los campos de seguimiento correctamente")
    void shouldUpdateDetails() {
        when(adoptionDetailRepository.findById(1L)).thenReturn(Optional.of(testDetail));
        when(adoptionDetailRepository.save(any(AdoptionDetail.class))).thenReturn(testDetail);
        when(adoptionDetailMapper.toDto(testDetail)).thenReturn(testDetailDTO);

        AdoptionDetailDTO result = adoptionDetailService.updateDetails(1L, testDetailDTO);

        assertThat(result).isNotNull();
        verify(adoptionDetailRepository, times(1)).save(testDetail);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un detalle que no existe")
    void shouldThrowExceptionWhenUpdatingNonExistentDetail() {
        when(adoptionDetailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adoptionDetailService.updateDetails(99L, testDetailDTO));

        verify(adoptionDetailRepository, never()).save(any());
    }
}