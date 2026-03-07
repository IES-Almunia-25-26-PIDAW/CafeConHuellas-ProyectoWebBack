package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.VaccineMapper;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import com.example.cafe_con_huellas.repository.VaccineRepository;
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
class VaccineServiceTest {

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private VaccineMapper vaccineMapper;

    @InjectMocks
    private VaccineService vaccineService;

    private Vaccine testVaccine;
    private VaccineDTO testVaccineDTO;

    @BeforeEach
    void setUp() {
        testVaccine = new Vaccine();
        testVaccine.setId(1L);
        testVaccine.setName("Rabia");

        testVaccineDTO = new VaccineDTO();
        testVaccineDTO.setId(1L);
        testVaccineDTO.setName("Rabia");
    }

    @Test
    @DisplayName("Debe devolver todas las vacunas del catálogo correctamente")
    void shouldFindAllVaccines() {
        when(vaccineRepository.findAll()).thenReturn(List.of(testVaccine));
        when(vaccineMapper.toDto(testVaccine)).thenReturn(testVaccineDTO);

        List<VaccineDTO> result = vaccineService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rabia");
    }

    @Test
    @DisplayName("Debe encontrar una vacuna por su ID")
    void shouldFindVaccineById() {
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccineMapper.toDto(testVaccine)).thenReturn(testVaccineDTO);

        VaccineDTO result = vaccineService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Rabia");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la vacuna no existe")
    void shouldThrowExceptionWhenVaccineNotFound() {
        when(vaccineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vaccineService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar una vacuna nueva correctamente")
    void shouldSaveNewVaccine() {
        // Sin ID porque es nueva
        testVaccineDTO.setId(null);
        // No existe duplicado
        when(vaccineRepository.existsByName("Rabia")).thenReturn(false);
        when(vaccineMapper.toEntity(testVaccineDTO)).thenReturn(testVaccine);
        when(vaccineRepository.save(any(Vaccine.class))).thenReturn(testVaccine);
        when(vaccineMapper.toDto(testVaccine)).thenReturn(testVaccineDTO);

        VaccineDTO result = vaccineService.save(testVaccineDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Rabia");
        verify(vaccineRepository, times(1)).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la vacuna ya existe en el catálogo")
    void shouldThrowExceptionWhenVaccineAlreadyExists() {
        testVaccineDTO.setId(null);
        // Ya existe una vacuna con ese nombre
        when(vaccineRepository.existsByName("Rabia")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> vaccineService.save(testVaccineDTO));

        verify(vaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar una vacuna existente correctamente")
    void shouldDeleteVaccineById() {
        when(vaccineRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> vaccineService.deleteById(1L));

        verify(vaccineRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar una vacuna que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentVaccine() {
        when(vaccineRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> vaccineService.deleteById(99L));

        verify(vaccineRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe encontrar una vacuna por su nombre exacto")
    void shouldFindVaccineByName() {
        when(vaccineRepository.findByName("Rabia")).thenReturn(Optional.of(testVaccine));
        when(vaccineMapper.toDto(testVaccine)).thenReturn(testVaccineDTO);

        VaccineDTO result = vaccineService.findByName("Rabia");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Rabia");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no existe vacuna con ese nombre")
    void shouldThrowExceptionWhenVaccineNameNotFound() {
        when(vaccineRepository.findByName("Inexistente")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vaccineService.findByName("Inexistente"));
    }

    @Test
    @DisplayName("Debe devolver true si existe una vacuna con ese nombre")
    void shouldReturnTrueWhenVaccineExists() {
        when(vaccineRepository.existsByName("Rabia")).thenReturn(true);

        boolean result = vaccineService.existsByName("Rabia");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si no existe una vacuna con ese nombre")
    void shouldReturnFalseWhenVaccineNotExists() {
        when(vaccineRepository.existsByName("Inexistente")).thenReturn(false);

        boolean result = vaccineService.existsByName("Inexistente");

        assertThat(result).isFalse();
    }
}