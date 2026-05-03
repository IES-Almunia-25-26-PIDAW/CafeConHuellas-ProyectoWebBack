package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserPetRelationshipMapper;
import com.example.cafe_con_huellas.model.entity.*;
import com.example.cafe_con_huellas.repository.AdoptionRequestRepository;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPetRelationshipServiceTest {

    @Mock
    private UserPetRelationshipRepository relationshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserPetRelationshipMapper relationshipMapper;

    @Mock
    private AdoptionRequestRepository adoptionRequestRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserPetRelationshipService relationshipService;

    private User testUser;
    private Pet testPet;
    private UserPetRelationship testRelationship;
    private UserPetRelationshipDTO testRelationshipDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Ana");
        testUser.setLastName1("Cruces");
        testUser.setEmail("ana@test.com");

        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("Firu");

        testRelationship = new UserPetRelationship();
        testRelationship.setId(1L);
        testRelationship.setUser(testUser);
        testRelationship.setPet(testPet);
        testRelationship.setRelationshipType(RelationshipType.ACOGIDA);
        testRelationship.setActive(true);
        testRelationship.setStartDate(LocalDate.now());

        testRelationshipDTO = new UserPetRelationshipDTO();
        testRelationshipDTO.setId(1L);
        testRelationshipDTO.setUserId(1L);
        testRelationshipDTO.setPetId(1L);
        testRelationshipDTO.setRelationshipType("ACOGIDA");
        testRelationshipDTO.setActive(true);
    }

    @Test
    @DisplayName("Debe devolver todas las relaciones correctamente")
    void shouldFindAllRelationships() {
        when(relationshipRepository.findAll()).thenReturn(List.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        List<UserPetRelationshipDTO> result = relationshipService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRelationshipType()).isEqualTo("ACOGIDA");
    }

    @Test
    @DisplayName("Debe encontrar una relación por su ID")
    void shouldFindRelationshipById() {
        when(relationshipRepository.findById(1L)).thenReturn(Optional.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        UserPetRelationshipDTO result = relationshipService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getRelationshipType()).isEqualTo("ACOGIDA");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la relación no existe")
    void shouldThrowExceptionWhenRelationshipNotFound() {
        when(relationshipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> relationshipService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar una nueva relación correctamente sin conflictos")
    void shouldSaveNewRelationship() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        // Sin vínculos activos previos
        when(relationshipRepository.findByPetId(1L)).thenReturn(Collections.emptyList());
        when(relationshipMapper.toEntity(testRelationshipDTO)).thenReturn(testRelationship);
        when(relationshipRepository.save(any(UserPetRelationship.class))).thenReturn(testRelationship);
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        UserPetRelationshipDTO result = relationshipService.save(testRelationshipDTO);

        assertThat(result).isNotNull();
        verify(relationshipRepository, times(1)).save(any(UserPetRelationship.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe al guardar")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> relationshipService.save(testRelationshipDTO));

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota no existe al guardar")
    void shouldThrowExceptionWhenPetNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> relationshipService.save(testRelationshipDTO));

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota ya está adoptada")
    void shouldThrowExceptionWhenPetAlreadyAdopted() {
        // Simulamos un vínculo activo de tipo ADOPCION
        UserPetRelationship adoptedRelationship = new UserPetRelationship();
        adoptedRelationship.setRelationshipType(RelationshipType.ADOPCION);
        adoptedRelationship.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(relationshipRepository.findByPetId(1L)).thenReturn(List.of(adoptedRelationship));

        assertThrows(BadRequestException.class,
                () -> relationshipService.save(testRelationshipDTO));

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota ya tiene acogida o adopción en curso")
    void shouldThrowExceptionWhenPetHasActiveAcogida() {
        // Simulamos un vínculo activo de tipo ACOGIDA
        UserPetRelationship activeAcogida = new UserPetRelationship();
        activeAcogida.setRelationshipType(RelationshipType.ACOGIDA);
        activeAcogida.setActive(true);

        // El nuevo vínculo también es ACOGIDA
        testRelationshipDTO.setRelationshipType("ACOGIDA");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(relationshipRepository.findByPetId(1L)).thenReturn(List.of(activeAcogida));

        assertThrows(BadRequestException.class,
                () -> relationshipService.save(testRelationshipDTO));

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar una relación existente correctamente")
    void shouldDeleteRelationshipById() {
        when(relationshipRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> relationshipService.deleteById(1L));

        verify(relationshipRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar una relación que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentRelationship() {
        when(relationshipRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> relationshipService.deleteById(99L));

        verify(relationshipRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe devolver las relaciones de un usuario concreto")
    void shouldFindRelationshipsByUserId() {
        when(relationshipRepository.findByUserId(1L)).thenReturn(List.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        List<UserPetRelationshipDTO> result = relationshipService.findByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe devolver las relaciones de una mascota concreta")
    void shouldFindRelationshipsByPetId() {
        when(relationshipRepository.findByPetId(1L)).thenReturn(List.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        List<UserPetRelationshipDTO> result = relationshipService.findByPetId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe devolver todas las relaciones activas")
    void shouldFindActiveRelationships() {
        when(relationshipRepository.findByActiveTrue()).thenReturn(List.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        List<UserPetRelationshipDTO> result = relationshipService.findActiveRelationships();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
    }

    @Test
    @DisplayName("Debe cerrar una relación activa con fecha proporcionada")
    void shouldEndRelationshipWithProvidedDate() {
        LocalDate endDate = LocalDate.now();
        when(relationshipRepository.findById(1L)).thenReturn(Optional.of(testRelationship));

        assertDoesNotThrow(() -> relationshipService.endRelationship(1L, endDate));

        assertThat(testRelationship.getActive()).isFalse();
        assertThat(testRelationship.getEndDate()).isEqualTo(endDate);
        verify(relationshipRepository, times(1)).save(testRelationship);
    }

    @Test
    @DisplayName("Debe cerrar una relación activa con fecha actual si no se proporciona")
    void shouldEndRelationshipWithCurrentDateWhenNullProvided() {
        when(relationshipRepository.findById(1L)).thenReturn(Optional.of(testRelationship));

        assertDoesNotThrow(() -> relationshipService.endRelationship(1L, null));

        assertThat(testRelationship.getActive()).isFalse();
        assertThat(testRelationship.getEndDate()).isEqualTo(LocalDate.now());
        verify(relationshipRepository, times(1)).save(testRelationship);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cerrar una relación que no existe")
    void shouldThrowExceptionWhenEndingNonExistentRelationship() {
        when(relationshipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> relationshipService.endRelationship(99L, LocalDate.now()));

        verify(relationshipRepository, never()).save(any());
    }

    // -------------------- save ADOPCION --------------------

    @Test
    @DisplayName("save() con tipo ADOPCION vincula la AdoptionRequest aprobada")
    void shouldLinkAdoptionRequestWhenSavingAdopcion() {
        // Preparamos el DTO como ADOPCION
        testRelationshipDTO.setRelationshipType("ADOPCION");

        AdoptionRequest adoptionRequest = new AdoptionRequest();
        adoptionRequest.setId(1L);
        adoptionRequest.setStatus(AdoptionRequestStatus.APROBADA);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(relationshipRepository.findByPetId(1L)).thenReturn(Collections.emptyList());
        when(relationshipMapper.toEntity(testRelationshipDTO)).thenReturn(testRelationship);
        when(relationshipRepository.save(any())).thenReturn(testRelationship);
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);
        when(adoptionRequestRepository.findByFormToken_UserIdAndFormToken_PetIdAndStatus(
                1L, 1L, AdoptionRequestStatus.APROBADA))
                .thenReturn(Optional.of(adoptionRequest));

        relationshipService.save(testRelationshipDTO);

        // Verificamos que se guardó la solicitud con la relación vinculada
        verify(adoptionRequestRepository).save(adoptionRequest);
        assertThat(adoptionRequest.getRelationship()).isEqualTo(testRelationship);
    }

    @Test
    @DisplayName("save() con tipo ADOPCION no falla si no hay AdoptionRequest aprobada")
    void shouldNotFailWhenNoApprovedAdoptionRequest() {
        testRelationshipDTO.setRelationshipType("ADOPCION");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(relationshipRepository.findByPetId(1L)).thenReturn(Collections.emptyList());
        when(relationshipMapper.toEntity(testRelationshipDTO)).thenReturn(testRelationship);
        when(relationshipRepository.save(any())).thenReturn(testRelationship);
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);
        when(adoptionRequestRepository.findByFormToken_UserIdAndFormToken_PetIdAndStatus(
                1L, 1L, AdoptionRequestStatus.APROBADA))
                .thenReturn(Optional.empty());

        // No debe lanzar excepción aunque no haya solicitud aprobada
        assertThatNoException().isThrownBy(() -> relationshipService.save(testRelationshipDTO));

        // Verificamos que no se intentó guardar ninguna solicitud
        verify(adoptionRequestRepository, never()).save(any());
    }

    // -------------------- update --------------------

    @Test
    @DisplayName("Debe enviar email de aceptación cuando active cambia a true")
    void shouldSendAcceptedEmailWhenActiveChangesToTrue() {
        // La relación empieza como inactiva
        testRelationship.setActive(false);

        // El DTO llega con active = true (el admin acepta)
        testRelationshipDTO.setActive(true);

        when(relationshipRepository.findById(1L)).thenReturn(Optional.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        relationshipService.update(1L, testRelationshipDTO);

        verify(emailService, times(1))
                .notifyRelationshipAccepted("ana@test.com", "Ana Cruces", "Firu", "ACOGIDA");
        verify(emailService, never())
                .notifyRelationshipRejected(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Debe enviar email de rechazo cuando active cambia a false")
    void shouldSendRejectedEmailWhenActiveChangesToFalse() {
        // La relación empieza como activa
        testRelationship.setActive(true);

        // El DTO llega con active = false (el admin rechaza)
        testRelationshipDTO.setActive(false);

        when(relationshipRepository.findById(1L)).thenReturn(Optional.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        relationshipService.update(1L, testRelationshipDTO);

        verify(emailService, times(1))
                .notifyRelationshipRejected("ana@test.com", "Ana Cruces", "Firu", "ACOGIDA");
        verify(emailService, never())
                .notifyRelationshipAccepted(any(), any(), any(), any());
    }

    @Test
    @DisplayName("No debe enviar email si active no cambia")
    void shouldNotSendEmailWhenActiveDoesNotChange() {
        // La relación empieza como activa y el DTO también llega como activa
        testRelationship.setActive(true);
        testRelationshipDTO.setActive(true);

        when(relationshipRepository.findById(1L)).thenReturn(Optional.of(testRelationship));
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        relationshipService.update(1L, testRelationshipDTO);

        verify(emailService, never()).notifyRelationshipAccepted(any(), any(), any(), any());
        verify(emailService, never()).notifyRelationshipRejected(any(), any(), any(), any());
    }

    // -------------------- saveAsUser --------------------

    @Test
    @DisplayName("saveAsUser() crea relación con active=false para usuario autenticado")
    void shouldSaveRelationshipAsUser() {
        testRelationshipDTO.setRelationshipType("ACOGIDA");
        testRelationship.setRelationshipType(RelationshipType.ACOGIDA);
        testRelationship.setActive(false);

        when(userRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        // explícito que no hay duplicado
        when(relationshipRepository.existsByUserIdAndPetIdAndRelationshipType(
                1L, 1L, RelationshipType.ACOGIDA)).thenReturn(false);
        when(relationshipRepository.findByPetId(1L)).thenReturn(Collections.emptyList());
        when(relationshipRepository.save(any(UserPetRelationship.class))).thenReturn(testRelationship);
        when(relationshipMapper.toDto(testRelationship)).thenReturn(testRelationshipDTO);

        UserPetRelationshipDTO result = relationshipService.saveAsUser("maria@example.com", testRelationshipDTO);

        assertThat(result).isNotNull();
        verify(relationshipRepository).save(argThat(r -> !r.getActive()));
    }

    @Test
    @DisplayName("saveAsUser() lanza BadRequestException si el tipo es ADOPCION")
    void shouldThrowWhenUserTriesToCreateAdopcionRelationship() {
        testRelationshipDTO.setRelationshipType("ADOPCION");

        assertThatThrownBy(() -> relationshipService.saveAsUser("maria@example.com", testRelationshipDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ADOPCION");

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveAsUser() lanza ResourceNotFoundException si el usuario autenticado no existe")
    void shouldThrowWhenAuthenticatedUserNotFound() {
        testRelationshipDTO.setRelationshipType("PASEO");

        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> relationshipService.saveAsUser("noexiste@test.com", testRelationshipDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveAsUser() lanza BadRequestException si la mascota ya está adoptada")
    void shouldThrowWhenPetIsAlreadyAdopted() {
        testRelationshipDTO.setRelationshipType("ACOGIDA");

        UserPetRelationship adoptedLink = new UserPetRelationship();
        adoptedLink.setRelationshipType(RelationshipType.ADOPCION);
        adoptedLink.setActive(true);

        when(userRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(relationshipRepository.findByPetId(1L)).thenReturn(List.of(adoptedLink));

        assertThatThrownBy(() -> relationshipService.saveAsUser("maria@example.com", testRelationshipDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("adoptada");

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveAsUser() lanza BadRequestException si ya existe una solicitud igual")
    void shouldThrowWhenDuplicateRelationshipAsUser() {
        testRelationshipDTO.setRelationshipType("ACOGIDA");

        when(userRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(relationshipRepository.existsByUserIdAndPetIdAndRelationshipType(
                1L, 1L, RelationshipType.ACOGIDA)).thenReturn(true);

        assertThatThrownBy(() -> relationshipService.saveAsUser("maria@example.com", testRelationshipDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("solicitud");

        verify(relationshipRepository, never()).save(any());
    }

}