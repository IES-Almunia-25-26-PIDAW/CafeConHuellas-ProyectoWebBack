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

/**
 * Servicio encargado de la creación y validación de tokens únicos para formularios de adopción.
 * <p>
 * Genera un UUID por cada solicitud, lo persiste con fecha de expiración
 * y envía el enlace al usuario por email. Garantiza que cada token
 * solo pueda usarse una vez.
 * </p>
 */
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

    /**
     * Genera un token único, lo persiste en base de datos y envía el enlace al usuario por email.
     * <p>
     * Verifica que el usuario y la mascota existan y que no haya ya
     * un formulario activo pendiente para esa combinación.
     * </p>
     *
     * @param userId identificador del usuario destinatario
     * @param petId  identificador de la mascota
     * @throws ResourceNotFoundException si el usuario o la mascota no existen
     * @throws BadRequestException si ya existe un formulario activo para esa combinación
     */
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

    /**
     * Valida que el token sea correcto, no haya expirado y no haya sido utilizado.
     *
     * @param token token UUID recibido desde el enlace del email
     * @return {@link AdoptionFormToken} con los datos del token validado
     * @throws ResourceNotFoundException si el token no existe
     * @throws BadRequestException si el token ha expirado o ya fue usado
     */
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

    /**
     * Marca un token como utilizado para evitar envíos duplicados del formulario.
     *
     * @param token token UUID a marcar como usado
     * @throws ResourceNotFoundException si el token no existe
     */
    @Transactional
    public void markTokenAsUsed(String token) {
        AdoptionFormToken formToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado."));

        // Marcamos como usado para que no pueda usarse de nuevo
        formToken.setUsed(true);
        tokenRepository.save(formToken);
    }
}