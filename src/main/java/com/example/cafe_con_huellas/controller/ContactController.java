package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.ContactRequestDTO;
import com.example.cafe_con_huellas.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el formulario de contacto público.
 * No requiere autenticación: cualquier visitante puede enviar un mensaje.
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    /**
     * Recibe el mensaje del formulario de contacto y lo reenvía al administrador por email.
     * Devuelve 204 No Content si el envío fue correcto.
     *
     * @param dto datos del formulario: nombre, email y mensaje del usuario
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendContactMessage(@Valid @RequestBody ContactRequestDTO dto) {
        emailService.notifyAdminContactForm(dto.getNombre(), dto.getEmail(), dto.getMensaje());
    }
}