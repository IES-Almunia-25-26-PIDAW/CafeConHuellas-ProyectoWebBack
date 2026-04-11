package com.example.cafe_con_huellas.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    // Mock del mensaje MIME que devuelve el mailSender al crear un nuevo correo
    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() throws MessagingException {
        // Inyectamos los @Value manualmente porque no cargamos contexto de Spring
        ReflectionTestUtils.setField(emailService, "adminEmail", "admin@test.com");
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@test.com");
        // Simulamos que createMimeMessage() devuelve nuestro mock en lugar de un mensaje real
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Debe enviar un correo simple correctamente")
    void shouldSendEmail() {
        emailService.sendEmail("destinatario@test.com", "Asunto test", "Cuerpo test");

        // Verificamos que se creo el mensaje y se envio exactamente una vez
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Debe notificar al admin cuando hay una solicitud de adopcion")
    void shouldNotifyAdminAdoptionRequest() {
        emailService.notifyAdminAdoptionRequest("Ana Cruces", "ana@test.com", "Firu");

        // Verificamos que se genero y envio el correo al admin
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Debe enviar confirmacion al usuario tras su solicitud de adopcion")
    void shouldConfirmAdoptionRequestToUser() {
        emailService.confirmAdoptionRequestToUser("ana@test.com", "Ana", "Firu");

        // Verificamos que se genero y envio el correo de confirmacion al usuario
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Debe enviar el enlace del formulario de adopcion con el token correcto")
    void shouldSendAdoptionFormLink() {
        emailService.sendAdoptionFormLink("ana@test.com", "Ana", "Firu", "uuid-token-123");

        // Verificamos que se genero y envio el correo con el enlace del formulario
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("No debe enviar correo si el mailSender falla")
    void shouldPropagateExceptionWhenMailSenderFails() {
        // Simulamos que el servidor de correo falla al intentar enviar
        doThrow(new RuntimeException("Error SMTP"))
                .when(mailSender).send(any(MimeMessage.class));

        // La excepcion debe propagarse hacia arriba
        assertThrows(RuntimeException.class,
                () -> emailService.sendEmail("ana@test.com", "Asunto", "Cuerpo"));
    }
}