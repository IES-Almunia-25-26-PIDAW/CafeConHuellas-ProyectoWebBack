package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.AdoptionRequestMapper;
import com.example.cafe_con_huellas.model.entity.*;
import com.example.cafe_con_huellas.repository.AdoptionFormTokenRepository;
import com.example.cafe_con_huellas.repository.AdoptionRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionRequestServiceTest {

    @Mock
    private AdoptionRequestRepository requestRepository;

    @Mock
    private AdoptionRequestMapper requestMapper;

    @Mock
    private AdoptionFormTokenRepository tokenRepository;

    @InjectMocks
    private AdoptionRequestService requestService;

    private AdoptionFormToken formToken;
    private AdoptionRequest adoptionRequest;
    private AdoptionRequestDTO adoptionRequestDTO;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirstName("María");
        user.setLastName1("García");
        user.setEmail("maria@example.com");

        Pet pet = new Pet();
        pet.setName("Firu");

        formToken = new AdoptionFormToken();
        formToken.setId(1L);
        formToken.setToken("abc123");
        formToken.setUser(user);
        formToken.setPet(pet);

        adoptionRequest = new AdoptionRequest();
        adoptionRequest.setId(1L);
        adoptionRequest.setFormToken(formToken);
        adoptionRequest.setStatus(AdoptionRequestStatus.PENDIENTE);

        adoptionRequestDTO = AdoptionRequestDTO.builder()
                .id(1L)
                .address("Calle Mayor 1")
                .city("Jerez")
                .housingType("PISO")
                .hasGarden(false)
                .hasOtherPets(false)
                .hasChildren(false)
                .hoursAlonePerDay(4)
                .experienceWithPets(true)
                .reasonForAdoption("Quiero darle un hogar a un perro que lo necesite")
                .agreesToFollowUp(true)
                .status(AdoptionRequestStatus.PENDIENTE)
                .build();
    }

    // -------------------- save --------------------

    @Test
    @DisplayName("save() guarda la solicitud correctamente y devuelve el DTO")
    void shouldSaveAdoptionRequest() {
        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(formToken));
        when(requestRepository.existsByFormTokenId(1L)).thenReturn(false);
        when(requestMapper.toEntity(any())).thenReturn(adoptionRequest);
        when(requestRepository.save(any())).thenReturn(adoptionRequest);
        when(requestMapper.toDto(any())).thenReturn(adoptionRequestDTO);

        AdoptionRequestDTO result = requestService.save("abc123", adoptionRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo("Calle Mayor 1");
        verify(requestRepository).save(any());
    }

    @Test
    @DisplayName("save() lanza BadRequestException si el token ya tiene una solicitud")
    void shouldThrowWhenTokenAlreadyHasRequest() {
        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(formToken));
        when(requestRepository.existsByFormTokenId(1L)).thenReturn(true);

        assertThatThrownBy(() -> requestService.save("abc123", adoptionRequestDTO))
                .isInstanceOf(BadRequestException.class);

        verify(requestRepository, never()).save(any());
    }

    @Test
    @DisplayName("save() lanza ResourceNotFoundException si el token no existe")
    void shouldThrowWhenTokenNotFound() {
        when(tokenRepository.findByToken("tokenInvalido")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.save("tokenInvalido", adoptionRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -------------------- findAll --------------------

    @Test
    @DisplayName("findAll() devuelve la lista de todas las solicitudes")
    void shouldReturnAllRequests() {
        when(requestRepository.findAll()).thenReturn(List.of(adoptionRequest));
        when(requestMapper.toDto(any())).thenReturn(adoptionRequestDTO);

        List<AdoptionRequestDTO> result = requestService.findAll();

        assertThat(result).hasSize(1);
    }

    // -------------------- findByUserEmail --------------------

    @Test
    @DisplayName("findByUserEmail() devuelve las solicitudes del usuario autenticado")
    void shouldReturnRequestsByUserEmail() {
        when(requestRepository.findByUserEmail("maria@example.com")).thenReturn(List.of(adoptionRequest));
        when(requestMapper.toDto(any())).thenReturn(adoptionRequestDTO);

        List<AdoptionRequestDTO> result = requestService.findByUserEmail("maria@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findByUserEmail() devuelve lista vacía si el usuario no tiene solicitudes")
    void shouldReturnEmptyWhenUserHasNoRequests() {
        when(requestRepository.findByUserEmail("noexiste@test.com")).thenReturn(List.of());

        List<AdoptionRequestDTO> result = requestService.findByUserEmail("noexiste@test.com");

        assertThat(result).isEmpty();
    }

    // -------------------- findByStatus --------------------

    @Test
    @DisplayName("findByStatus() filtra correctamente por estado PENDIENTE")
    void shouldReturnRequestsByStatus() {
        when(requestRepository.findByStatus(AdoptionRequestStatus.PENDIENTE))
                .thenReturn(List.of(adoptionRequest));
        when(requestMapper.toDto(any())).thenReturn(adoptionRequestDTO);

        List<AdoptionRequestDTO> result = requestService.findByStatus(AdoptionRequestStatus.PENDIENTE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AdoptionRequestStatus.PENDIENTE);
    }

    // -------------------- findById --------------------

    @Test
    @DisplayName("findById() devuelve el DTO cuando existe")
    void shouldReturnRequestById() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(adoptionRequest));
        when(requestMapper.toDto(any())).thenReturn(adoptionRequestDTO);

        AdoptionRequestDTO result = requestService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById() lanza ResourceNotFoundException si no existe")
    void shouldThrowWhenRequestNotFound() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -------------------- updateStatus --------------------

    @Test
    @DisplayName("updateStatus() cambia el estado correctamente a APROBADA")
    void shouldUpdateStatusToApproved() {
        AdoptionRequestDTO approvedDTO = AdoptionRequestDTO.builder()
                .id(1L)
                .status(AdoptionRequestStatus.APROBADA)
                .build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(adoptionRequest));
        when(requestRepository.save(any())).thenReturn(adoptionRequest);
        when(requestMapper.toDto(any())).thenReturn(approvedDTO);

        AdoptionRequestDTO result = requestService.updateStatus(1L, AdoptionRequestStatus.APROBADA);

        assertThat(result.getStatus()).isEqualTo(AdoptionRequestStatus.APROBADA);
        verify(requestRepository).save(adoptionRequest);
    }

    @Test
    @DisplayName("updateStatus() lanza ResourceNotFoundException si la solicitud no existe")
    void shouldThrowWhenUpdatingNonExistentRequest() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.updateStatus(99L, AdoptionRequestStatus.APROBADA))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}