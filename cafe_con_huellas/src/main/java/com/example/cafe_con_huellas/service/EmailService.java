package com.example.cafe_con_huellas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// Servicio encargado de enviar correos electrónicos automáticos
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Email del admin que recibirá las notificaciones, viene de application.properties
    @Value("${app.admin.email}")
    private String adminEmail;

    // Email desde el que se envían los correos, viene de application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    // Método genérico para enviar cualquier correo simple
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Remitente
        message.setFrom(fromEmail);
        // Destinatario
        message.setTo(to);
        // Asunto
        message.setSubject(subject);
        // Cuerpo del mensaje
        message.setText(body);

        mailSender.send(message);
    }

    // Notifica al admin cuando un usuario solicita adoptar una mascota
    public void notifyAdminAdoptionRequest(String userName, String userEmail, String petName) {
        String subject = "Nueva solicitud de adopción - " + petName;
        String body = """
                ¡Hola Administrador!
                
                Has recibido una nueva solicitud de adopción:
                
                - Usuario: %s
                - Email de contacto: %s
                - Mascota solicitada: %s
                
                Accede al panel de administración para gestionar la solicitud.
                
                — Café con Huellas
                """.formatted(userName, userEmail, petName);

        sendEmail(adminEmail, subject, body);
    }

    // Envía confirmación al usuario cuando su solicitud ha sido registrada
    public void confirmAdoptionRequestToUser(String userEmail, String userName, String petName) {
        String subject = "Solicitud de adopción recibida - " + petName;
        String body = """
                ¡Hola %s!
                
                ¡Gracias por interesarte en %s! Hemos recibido tu solicitud de adopción correctamente.
                
                    ¿Qué sigue ahora?
                    Nuestro equipo de voluntarios revisará tu perfil para asegurar que tú y %s seáis el "match" ideal.
                    Solemos responder en un plazo de 48 a 72 horas. 
                
                    Si tienes alguna duda urgente mientras tanto, puedes responder directamente a este correo.
                
                    Gracias por tu paciencia y por elegir adoptar. ¡Ojalá muy pronto %s forme parte de tu familia! ❤️
                
                — Café con Huellas
                """.formatted(userName, petName, petName, petName);

        sendEmail(userEmail, subject, body);
    }

    // Envía un formulario público con token único al usuario interesado
    public void sendAdoptionFormLink(String userEmail, String userName, String petName, String token) {
        String subject = "Formulario de adopción - " + petName;
        String body = """
                ¡Hola %s!
                
                Para continuar con el proceso de adopción de %s, 
                necesitamos que rellenes el siguiente formulario:
                
                🔗 %s
                
                Este enlace es único y personal, no lo compartas con nadie.
                Expirará en 48 horas.
                
                — Café con Huellas
                """.formatted(userName, petName, buildFormLink(token));

        sendEmail(userEmail, subject, body);
    }

    // Construye la URL del formulario público con el token único
    private String buildFormLink(String token) {
        // En producción esto vendría de application.properties con la URL real del frontend
        return "http://localhost:4200/adopcion/formulario/" + token;
    }
}