package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import com.example.cafe_con_huellas.service.AdoptionFormTokenService;
import com.example.cafe_con_huellas.service.AdoptionRequestService;
import com.example.cafe_con_huellas.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar el flujo del formulario público de adopción.
 * <p>
 * Implementa un proceso en tres pasos: el administrador envía el formulario
 * por email al usuario interesado, el usuario valida su token de acceso,
 * y finalmente envía el formulario cumplimentado.
 * Los endpoints de validación y envío son públicos y no requieren autenticación.
 * </p>
 */
@RestController
@RequestMapping("/api/adoption-form")
@RequiredArgsConstructor
public class AdoptionFormController {

    private final AdoptionFormTokenService tokenService;
    private final AdoptionRequestService adoptionRequestService; // NUEVO
    private final EmailService emailService;


    /**
     * Genera un token de acceso y envía el formulario de adopción por email al usuario.
     * <p>
     * El token tiene una expiración limitada y solo puede usarse una vez.
     * Requiere rol ADMIN.
     * </p>
     *
     * @param userId identificador del usuario destinatario
     * @param petId  identificador de la mascota sobre la que se solicita la adopción
     */
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void sendAdoptionForm(
            @RequestParam Long userId,
            @RequestParam Long petId) {

        // El servicio genera el token, lo guarda y envía el correo
        tokenService.generateAndSendFormToken(userId, petId);
    }


    /**
     * Valida el token recibido por email y devuelve los datos para mostrar el formulario.
     * <p>
     * Endpoint público accesible desde el enlace del correo.
     * Verifica que el token sea válido, no haya expirado y no haya sido utilizado.
     * </p>
     *
     * @param token token único incluido en el enlace del correo
     * @return {@link AdoptionFormTokenResponse} con los datos del usuario y la mascota
     */
    @GetMapping("/validate/{token}")
    public AdoptionFormTokenResponse validateToken(@PathVariable String token) {

        // Validamos que el token sea correcto, no haya expirado y no esté usado
        AdoptionFormToken formToken = tokenService.validateToken(token);

        // Devolvemos los datos necesarios para que el frontend muestre el formulario
        return new AdoptionFormTokenResponse(
                formToken.getUser().getFirstName() + " " + formToken.getUser().getLastName1(),
                formToken.getUser().getEmail(),
                formToken.getPet().getName(),
                formToken.getPet().getBreed(),
                formToken.getExpiresAt().toString()
        );
    }


    /**
     * Procesa y guarda el formulario de adopción cumplimentado por el usuario.
     * <p>
     * Endpoint público identificado mediante token. Tras guardar la solicitud,
     * notifica al administrador y envía confirmación al usuario por email.
     * El token se marca como usado para evitar envíos duplicados.
     * </p>
     *
     * @param token   token único que identifica al usuario y la mascota
     * @param request datos del formulario rellenado por el usuario
     */
    @PostMapping("/submit/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitAdoptionForm(
            @PathVariable String token,
            @RequestBody AdoptionFormRequest request) {

        // Validamos el token antes de procesar el formulario
        AdoptionFormToken formToken = tokenService.validateToken(token);

        // Construimos el DTO y guardamos la solicitud en base de datos
        AdoptionRequestDTO dto = AdoptionRequestDTO.builder()
                .address(request.address())
                .city(request.city())
                .housingType(request.housingType())
                .hasGarden(Boolean.parseBoolean(request.hasGarden()))
                .hasOtherPets(Boolean.parseBoolean(request.hasOtherPets()))
                .hasChildren(Boolean.parseBoolean(request.hasChildren()))
                .hoursAlonePerDay(request.hoursAlonePerDay())
                .experienceWithPets(Boolean.parseBoolean(request.experienceWithPets()))
                .reasonForAdoption(request.reasonForAdoption())
                .agreesToFollowUp(Boolean.parseBoolean(request.agreesToFollowUp()))
                .additionalInfo(request.additionalInfo())
                .build();

        adoptionRequestService.save(token, dto);

        // Notificamos al admin con los datos del formulario rellenado
        emailService.notifyAdminAdoptionRequest(
                formToken.getUser().getFirstName() + " " + formToken.getUser().getLastName1(),
                formToken.getUser().getEmail(),
                formToken.getPet().getName()
        );

        // Confirmamos al usuario que su solicitud fue recibida
        emailService.confirmAdoptionRequestToUser(
                formToken.getUser().getEmail(),
                formToken.getUser().getFirstName(),
                formToken.getPet().getName()
        );

        // Marcamos el token como usado para que no pueda enviarse dos veces
        tokenService.markTokenAsUsed(token);
    }

    // DTO interno para la respuesta de validación del token
    record AdoptionFormTokenResponse(
            String userName,
            String userEmail,
            String petName,
            String petBreed,
            String expiresAt
    ) {}

    // DTO interno con los datos que rellena el usuario en el formulario
    // Mantenemos String para los booleanos por compatibilidad con el frontend
    record AdoptionFormRequest(
            String address,
            String city,
            String housingType,
            String hasGarden,
            String hasOtherPets,
            String hasChildren,
            Integer hoursAlonePerDay,
            String experienceWithPets,
            String reasonForAdoption,
            String agreesToFollowUp,
            String additionalInfo
    ) {}
}