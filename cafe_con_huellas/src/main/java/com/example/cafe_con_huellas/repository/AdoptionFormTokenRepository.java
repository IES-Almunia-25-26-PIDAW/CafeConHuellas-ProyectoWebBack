package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio para gestionar los tokens únicos de los formularios de adopción
@Repository
public interface AdoptionFormTokenRepository extends JpaRepository<AdoptionFormToken, Long> {

    // Busca un token por su valor único (viene en la URL del formulario)
    Optional<AdoptionFormToken> findByToken(String token);

    // Comprueba si ya existe un token activo para un usuario y mascota concretos
    boolean existsByUserIdAndPetIdAndUsedFalse(Long userId, Long petId);
}