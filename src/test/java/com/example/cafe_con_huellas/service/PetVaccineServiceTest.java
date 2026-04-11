package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetVaccineMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.PetVaccineRepository;
import com.example.cafe_con_huellas.repository.VaccineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetVaccineServiceTest {

    @Mock
    private PetVaccineRepository petVaccineRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private PetVaccineMapper petVaccineMapper;

    @InjectMocks
    private PetVaccineService petVaccineService;

    private Pet testPet;
    private Vaccine testVaccine;
    private PetVaccine testPetVaccine;
    private PetVaccineDTO testPetVaccineDTO;

    @BeforeEach
    void setUp() {
        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("Firu");

        testVaccine = new Vaccine();
        testVaccine.setId(1L);
        testVaccine.setName("Rabia");

        testPetVaccine = new PetVaccine();
        testPetVaccine.setId(1L);
        testPetVaccine.setPet(testPet);
        testPetVaccine.setVaccine(testVaccine);
        testPetVaccine.setDateAdministered(LocalDate.now().minusDays(10));
        testPetVaccine.setNextDoseDate(LocalDate.now().plusMonths(6));

        testPetVaccineDTO = new PetVaccineDTO();
        testPetVaccineDTO.setId(1L);
        testPetVaccineDTO.setPetId(1L);
        testPetVaccineDTO.setVaccineId(1L);
        testPetVaccineDTO.setDateAdministered(LocalDate.now().minusDays(10));
        testPetVaccineDTO.setNextDoseDate(LocalDate.now().plusMonths(6));
    }

    @Test
    @DisplayName("Debe devolver todo el historial de vacunación correctamente")
    void shouldFindAllVaccines() {
        when(petVaccineRepository.findAll()).thenReturn(List.of(testPetVaccine));
        when(petVaccineMapper.toDto(testPetVaccine)).thenReturn(testPetVaccineDTO);

        List<PetVaccineDTO> result = petVaccineService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe encontrar un registro de vacunación por su ID")
    void shouldFindVaccineById() {
        when(petVaccineRepository.findById(1L)).thenReturn(Optional.of(testPetVaccine));
        when(petVaccineMapper.toDto(testPetVaccine)).thenReturn(testPetVaccineDTO);

        PetVaccineDTO result = petVaccineService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getVaccineId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el registro no existe")
    void shouldThrowExceptionWhenVaccineRecordNotFound() {
        when(petVaccineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> petVaccineService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar un registro de vacunación correctamente")
    void shouldSaveVaccineRecord() {
        when(petVaccineMapper.toEntity(testPetVaccineDTO)).thenReturn(testPetVaccine);
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(petVaccineRepository.save(any(PetVaccine.class))).thenReturn(testPetVaccine);
        when(petVaccineMapper.toDto(testPetVaccine)).thenReturn(testPetVaccineDTO);

        PetVaccineDTO result = petVaccineService.save(testPetVaccineDTO);

        assertThat(result).isNotNull();
        verify(petVaccineRepository, times(1)).save(any(PetVaccine.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la fecha de administración es futura")
    void shouldThrowExceptionWhenDateIsInFuture() {
        testPetVaccineDTO.setDateAdministered(LocalDate.now().plusDays(1));

        assertThrows(BadRequestException.class,
                () -> petVaccineService.save(testPetVaccineDTO));

        verify(petVaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota no existe al guardar")
    void shouldThrowExceptionWhenPetNotFound() {
        when(petVaccineMapper.toEntity(testPetVaccineDTO)).thenReturn(testPetVaccine);
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> petVaccineService.save(testPetVaccineDTO));

        verify(petVaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el tipo de vacuna no existe al guardar")
    void shouldThrowExceptionWhenVaccineNotFound() {
        when(petVaccineMapper.toEntity(testPetVaccineDTO)).thenReturn(testPetVaccine);
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> petVaccineService.save(testPetVaccineDTO));

        verify(petVaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar un registro de vacunación existente correctamente")
    void shouldDeleteVaccineRecord() {
        when(petVaccineRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> petVaccineService.deleteById(1L));

        verify(petVaccineRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un registro que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentRecord() {
        when(petVaccineRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> petVaccineService.deleteById(99L));

        verify(petVaccineRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe devolver el historial de vacunas de una mascota concreta")
    void shouldFindVaccinesByPetId() {
        when(petVaccineRepository.findByPetId(1L)).thenReturn(List.of(testPetVaccine));
        when(petVaccineMapper.toDto(testPetVaccine)).thenReturn(testPetVaccineDTO);

        List<PetVaccineDTO> result = petVaccineService.findByPetId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe devolver las vacunas con próxima dosis pendiente")
    void shouldFindUpcomingVaccines() {
        LocalDate fromDate = LocalDate.now();
        when(petVaccineRepository.findByNextDoseDateAfter(fromDate))
                .thenReturn(List.of(testPetVaccine));
        when(petVaccineMapper.toDto(testPetVaccine)).thenReturn(testPetVaccineDTO);

        List<PetVaccineDTO> result = petVaccineService.findUpcomingVaccines(fromDate);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Debe devolver las vacunas con refuerzo vencido")
    void shouldFindOverdueVaccines() {
        when(petVaccineRepository.findByNextDoseDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(testPetVaccine));
        when(petVaccineMapper.toDto(testPetVaccine)).thenReturn(testPetVaccineDTO);

        List<PetVaccineDTO> result = petVaccineService.findOverdueVaccines();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Debe actualizar la información médica de un registro correctamente")
    void shouldUpdateMedicalInfo() {
        when(petVaccineRepository.findById(1L)).thenReturn(Optional.of(testPetVaccine));
        when(petVaccineRepository.save(any(PetVaccine.class))).thenReturn(testPetVaccine);
        when(petVaccineMapper.toDto(testPetVaccine)).thenReturn(testPetVaccineDTO);

        PetVaccineDTO result = petVaccineService.updateMedicalInfo(1L, testPetVaccineDTO);

        assertThat(result).isNotNull();
        verify(petVaccineRepository, times(1)).save(testPetVaccine);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un registro que no existe")
    void shouldThrowExceptionWhenUpdatingNonExistentRecord() {
        when(petVaccineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> petVaccineService.updateMedicalInfo(99L, testPetVaccineDTO));

        verify(petVaccineRepository, never()).save(any());
    }
}