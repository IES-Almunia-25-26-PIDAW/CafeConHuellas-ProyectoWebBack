package com.example.cafe_con_huellas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    // Inyectamos los @Value manualmente porque no cargamos contexto de Spring
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "adminEmail", "admin@test.com");
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@test.com");
    }

    @Test
    @DisplayName("Debe enviar un correo simple correctamente")
    void shouldSendEmail() {
        // Capturamos el mensaje que se envía al mailSender
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail("destinatario@test.com", "Asunto test", "Cuerpo test");

        // Verificamos que se llamó al mailSender exactamente una vez
        verify(mailSender, times(1)).send(captor.capture());

        // Verificamos los campos del mensaje enviado
        SimpleMailMessage mensaje = captor.getValue();
        assertThat(mensaje.getTo()).contains("destinatario@test.com");
        assertThat(mensaje.getSubject()).isEqualTo("Asunto test");
        assertThat(mensaje.getText()).isEqualTo("Cuerpo test");
        assertThat(mensaje.getFrom()).isEqualTo("noreply@test.com");
    }

    @Test
    @DisplayName("Debe notificar al admin cuando hay una solicitud de adopción")
    void shouldNotifyAdminAdoptionRequest() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.notifyAdminAdoptionRequest("Ana Cruces", "ana@test.com", "Firu");

        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensaje = captor.getValue();
        // El correo debe ir al admin
        assertThat(mensaje.getTo()).contains("admin@test.com");
        // El asunto debe incluir el nombre de la mascota
        assertThat(mensaje.getSubject()).contains("Firu");
        // El cuerpo debe incluir el nombre y email del usuario
        assertThat(mensaje.getText()).contains("Ana Cruces");
        assertThat(mensaje.getText()).contains("ana@test.com");
    }

    @Test
    @DisplayName("Debe enviar confirmación al usuario tras su solicitud de adopción")
    void shouldConfirmAdoptionRequestToUser() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.confirmAdoptionRequestToUser("ana@test.com", "Ana", "Firu");

        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensaje = captor.getValue();
        // El correo debe ir al usuario, no al admin
        assertThat(mensaje.getTo()).contains("ana@test.com");
        assertThat(mensaje.getSubject()).contains("Firu");
        // El cuerpo debe saludar al usuario por su nombre
        assertThat(mensaje.getText()).contains("Ana");
        assertThat(mensaje.getText()).contains("Firu");
    }

    @Test
    @DisplayName("Debe enviar el enlace del formulario de adopción con el token correcto")
    void shouldSendAdoptionFormLink() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendAdoptionFormLink("ana@test.com", "Ana", "Firu", "uuid-token-123");

        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensaje = captor.getValue();
        assertThat(mensaje.getTo()).contains("ana@test.com");
        assertThat(mensaje.getSubject()).contains("Firu");
        // El cuerpo debe contener el token en el enlace
        assertThat(mensaje.getText()).contains("uuid-token-123");
        assertThat(mensaje.getText()).contains("localhost:4200/adopcion/formulario/");
    }

    @Test
    @DisplayName("No debe enviar correo si el mailSender falla")
    void shouldPropagateExceptionWhenMailSenderFails() {
        // Simulamos que el servidor de correo falla
        doThrow(new RuntimeException("Error SMTP"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // La excepción debe propagarse
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> emailService.sendEmail("ana@test.com", "Asunto", "Cuerpo"));
    }
}