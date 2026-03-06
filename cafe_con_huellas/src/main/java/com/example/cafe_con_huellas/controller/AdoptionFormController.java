package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import com.example.cafe_con_huellas.service.AdoptionFormTokenService;
import com.example.cafe_con_huellas.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Controlador para gestionar el flujo del formulario público de adopción
@RestController
@RequestMapping("/api/adoption-form")
@RequiredArgsConstructor
public class AdoptionFormController {

    private final AdoptionFormTokenService tokenService;
    private final EmailService emailService;

    // Endpoint protegido: el admin envía el formulario a un usuario interesado
    // Genera el token y manda el correo automáticamente
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void sendAdoptionForm(
            @RequestParam Long userId,
            @RequestParam Long petId) {

        // El servicio genera el token, lo guarda y envía el correo
        tokenService.generateAndSendFormToken(userId, petId);
    }

    // Endpoint PÚBLICO: el usuario accede desde el enlace del correo
    // No necesita estar logueado porque el token es su identificación
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

    // Endpoint PÚBLICO: el usuario envía el formulario rellenado
    // No necesita login, el token le identifica
    @PostMapping("/submit/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitAdoptionForm(
            @PathVariable String token,
            @RequestBody AdoptionFormRequest request) {

        // Validamos el token antes de procesar el formulario
        AdoptionFormToken formToken = tokenService.validateToken(token);

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
    record AdoptionFormRequest(
            String address,
            String city,
            String housingType,
            String hasGarden,
            String hasOtherPets,
            String additionalInfo
    ) {}
}