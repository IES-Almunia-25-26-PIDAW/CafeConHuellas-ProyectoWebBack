package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.UserPetFavoriteDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.UserPetFavoriteMapper;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.UserPetFavoriteRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
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
class UserPetFavoriteServiceTest {

    @Mock
    private UserPetFavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserPetFavoriteMapper favoriteMapper;

    @InjectMocks
    private UserPetFavoriteService favoriteService;

    private User testUser;
    private Pet testPet;
    private UserPetFavorite testFavorite;
    private UserPetFavoriteDTO testFavoriteDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Ana");

        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("Firu");

        testFavorite = new UserPetFavorite();
        testFavorite.setId(1L);
        testFavorite.setUser(testUser);
        testFavorite.setPet(testPet);

        testFavoriteDTO = new UserPetFavoriteDTO();
        testFavoriteDTO.setId(1L);
        testFavoriteDTO.setUserId(1L);
        testFavoriteDTO.setPetId(1L);
    }

    @Test
    @DisplayName("Debe devolver todos los favoritos correctamente")
    void shouldFindAllFavorites() {
        when(favoriteRepository.findAll()).thenReturn(List.of(testFavorite));
        when(favoriteMapper.toDto(testFavorite)).thenReturn(testFavoriteDTO);

        List<UserPetFavoriteDTO> result = favoriteService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe encontrar un favorito por su ID")
    void shouldFindFavoriteById() {
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(testFavorite));
        when(favoriteMapper.toDto(testFavorite)).thenReturn(testFavoriteDTO);

        UserPetFavoriteDTO result = favoriteService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPetId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el favorito no existe")
    void shouldThrowExceptionWhenFavoriteNotFound() {
        when(favoriteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar un nuevo favorito correctamente")
    void shouldSaveNewFavorite() {
        // No existe duplicado
        when(favoriteRepository.existsByUserIdAndPetId(1L, 1L)).thenReturn(false);
        when(favoriteMapper.toEntity(testFavoriteDTO)).thenReturn(testFavorite);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(favoriteRepository.save(any(UserPetFavorite.class))).thenReturn(testFavorite);
        when(favoriteMapper.toDto(testFavorite)).thenReturn(testFavoriteDTO);

        UserPetFavoriteDTO result = favoriteService.save(testFavoriteDTO);

        assertThat(result).isNotNull();
        verify(favoriteRepository, times(1)).save(any(UserPetFavorite.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota ya está en favoritos")
    void shouldThrowExceptionWhenAlreadyFavorite() {
        when(favoriteRepository.existsByUserIdAndPetId(1L, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> favoriteService.save(testFavoriteDTO));

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe al guardar favorito")
    void shouldThrowExceptionWhenUserNotFound() {
        when(favoriteRepository.existsByUserIdAndPetId(1L, 1L)).thenReturn(false);
        when(favoriteMapper.toEntity(testFavoriteDTO)).thenReturn(testFavorite);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.save(testFavoriteDTO));

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la mascota no existe al guardar favorito")
    void shouldThrowExceptionWhenPetNotFound() {
        when(favoriteRepository.existsByUserIdAndPetId(1L, 1L)).thenReturn(false);
        when(favoriteMapper.toEntity(testFavoriteDTO)).thenReturn(testFavorite);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.save(testFavoriteDTO));

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar un favorito existente correctamente")
    void shouldDeleteFavoriteById() {
        when(favoriteRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> favoriteService.deleteById(1L));

        verify(favoriteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un favorito que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentFavorite() {
        when(favoriteRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.deleteById(99L));

        verify(favoriteRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe devolver los favoritos de un usuario concreto")
    void shouldFindFavoritesByUserId() {
        when(favoriteRepository.findByUserId(1L)).thenReturn(List.of(testFavorite));
        when(favoriteMapper.toDto(testFavorite)).thenReturn(testFavoriteDTO);

        List<UserPetFavoriteDTO> result = favoriteService.findByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe devolver true si la mascota es favorita del usuario")
    void shouldReturnTrueWhenIsFavorite() {
        when(favoriteRepository.existsByUserIdAndPetId(1L, 1L)).thenReturn(true);

        boolean result = favoriteService.isFavorite(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si la mascota no es favorita del usuario")
    void shouldReturnFalseWhenIsNotFavorite() {
        when(favoriteRepository.existsByUserIdAndPetId(1L, 99L)).thenReturn(false);

        boolean result = favoriteService.isFavorite(1L, 99L);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Debe eliminar un favorito por userId y petId correctamente")
    void shouldRemoveFavorite() {
        when(favoriteRepository.existsByUserIdAndPetId(1L, 1L)).thenReturn(true);

        assertDoesNotThrow(() -> favoriteService.removeFavorite(1L, 1L));

        verify(favoriteRepository, times(1)).deleteByUserIdAndPetId(1L, 1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar favorito que no existe")
    void shouldThrowExceptionWhenRemovingNonExistentFavorite() {
        when(favoriteRepository.existsByUserIdAndPetId(1L, 99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.removeFavorite(1L, 99L));

        verify(favoriteRepository, never()).deleteByUserIdAndPetId(any(), any());
    }
}