package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.AdoptionFormTokenRepository;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionFormTokenServiceTest {

    @Mock
    private AdoptionFormTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdoptionFormTokenService tokenService;

    private User testUser;
    private Pet testPet;
    private AdoptionFormToken testToken;

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

        testToken = AdoptionFormToken.builder()
                .token("uuid-test-token-123")
                .user(testUser)
                .pet(testPet)
                .build();
        // Simulamos que @PrePersist se ejecutó
        testToken.setCreatedAt(LocalDateTime.now());
        testToken.setExpiresAt(LocalDateTime.now().plusHours(48));
        testToken.setUsed(false);
    }

    @Test
    @DisplayName("Debe generar y enviar el token correctamente")
    void shouldGenerateAndSendFormToken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        // No existe token activo previo
        when(tokenRepository.existsByUserIdAndPetIdAndUsedFalse(1L, 1L)).thenReturn(false);
        when(tokenRepository.save(any(AdoptionFormToken.class))).thenReturn(testToken);

        // No lanza excepción
        assertDoesNotThrow(() -> tokenService.generateAndSendFormToken(1L, 1L));

        // Verificamos que se guardó el token y se envió el correo
        verify(tokenRepository, times(1)).save(any(AdoptionFormToken.class));
        verify(emailService, times(1)).sendAdoptionFormLink(
                anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tokenService.generateAndSendFormToken(99L, 1L));

        // Nunca debe guardarse el token ni enviarse correo
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendAdoptionFormLink(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota no existe")
    void shouldThrowExceptionWhenPetNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tokenService.generateAndSendFormToken(1L, 99L));

        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un token activo para ese usuario y mascota")
    void shouldThrowExceptionWhenActiveTokenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        // Ya existe un token activo
        when(tokenRepository.existsByUserIdAndPetIdAndUsedFalse(1L, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> tokenService.generateAndSendFormToken(1L, 1L));

        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe validar correctamente un token válido")
    void shouldValidateValidToken() {
        when(tokenRepository.findByToken("uuid-test-token-123"))
                .thenReturn(Optional.of(testToken));

        AdoptionFormToken result = tokenService.validateToken("uuid-test-token-123");

        assertThat(result).isNotNull();
        assertThat(result.getUsed()).isFalse();
    }

    @Test
    @DisplayName("Debe lanzar excepción si el token no existe")
    void shouldThrowExceptionWhenTokenNotFound() {
        when(tokenRepository.findByToken("token-invalido"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tokenService.validateToken("token-invalido"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el token ha expirado")
    void shouldThrowExceptionWhenTokenExpired() {
        // Simulamos un token expirado
        testToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("uuid-test-token-123"))
                .thenReturn(Optional.of(testToken));

        assertThrows(BadRequestException.class,
                () -> tokenService.validateToken("uuid-test-token-123"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el token ya fue usado")
    void shouldThrowExceptionWhenTokenAlreadyUsed() {
        // Simulamos un token ya usado
        testToken.setUsed(true);
        when(tokenRepository.findByToken("uuid-test-token-123"))
                .thenReturn(Optional.of(testToken));

        assertThrows(BadRequestException.class,
                () -> tokenService.validateToken("uuid-test-token-123"));
    }

    @Test
    @DisplayName("Debe marcar el token como usado correctamente")
    void shouldMarkTokenAsUsed() {
        when(tokenRepository.findByToken("uuid-test-token-123"))
                .thenReturn(Optional.of(testToken));

        tokenService.markTokenAsUsed("uuid-test-token-123");

        // Verificamos que se marcó como usado y se guardó
        assertThat(testToken.getUsed()).isTrue();
        verify(tokenRepository, times(1)).save(testToken);
    }

    @Test
    @DisplayName("Debe lanzar excepción al marcar como usado un token inexistente")
    void shouldThrowExceptionWhenMarkingNonExistentToken() {
        when(tokenRepository.findByToken("token-inexistente"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tokenService.markTokenAsUsed("token-inexistente"));
    }
}