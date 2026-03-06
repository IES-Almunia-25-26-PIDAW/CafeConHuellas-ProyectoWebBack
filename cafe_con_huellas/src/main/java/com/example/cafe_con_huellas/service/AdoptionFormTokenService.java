package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.AdoptionFormTokenRepository;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// Servicio que gestiona la creación y validación de tokens únicos para formularios de adopción
@Service
@RequiredArgsConstructor
public class AdoptionFormTokenService {

    private final AdoptionFormTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final EmailService emailService;

    // URL del frontend, viene de application.properties
    @Value("${app.frontend.url}")
    private String frontendUrl;

    // Genera un token único, lo guarda en BD y envía el enlace por correo al usuario
    @Transactional
    public void generateAndSendFormToken(Long userId, Long petId) {

        // Verificamos que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        // Verificamos que la mascota existe
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + petId));

        // Evitamos enviar un segundo token si ya hay uno activo para este usuario y mascota
        if (tokenRepository.existsByUserIdAndPetIdAndUsedFalse(userId, petId)) {
            throw new BadRequestException("Ya existe un formulario activo para este usuario y mascota. Revisa tu correo.");
        }

        // Generamos un UUID aleatorio como token único
        String token = UUID.randomUUID().toString();

        // Guardamos el token en la base de datos
        AdoptionFormToken formToken = AdoptionFormToken.builder()
                .token(token)
                .user(user)
                .pet(pet)
                .build();

        tokenRepository.save(formToken);

        // Construimos el nombre completo del usuario para el correo
        String userName = user.getFirstName() + " " + user.getLastName1();

        // Enviamos el correo con el enlace único al usuario
        emailService.sendAdoptionFormLink(
                user.getEmail(),
                userName,
                pet.getName(),
                token
        );
    }

    // Valida que el token existe, no ha expirado y no ha sido usado
    @Transactional(readOnly = true)
    public AdoptionFormToken validateToken(String token) {

        // Buscamos el token en la BD
        AdoptionFormToken formToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no válido o inexistente."));

        // Comprobamos que no haya expirado
        if (formToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Este enlace ha expirado. Solicita uno nuevo.");
        }

        // Comprobamos que no haya sido usado ya
        if (formToken.getUsed()) {
            throw new BadRequestException("Este enlace ya fue utilizado anteriormente.");
        }

        return formToken;
    }

    // Marca el token como usado una vez el formulario ha sido enviado
    @Transactional
    public void markTokenAsUsed(String token) {
        AdoptionFormToken formToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado."));

        // Marcamos como usado para que no pueda usarse de nuevo
        formToken.setUsed(true);
        tokenRepository.save(formToken);
    }
}