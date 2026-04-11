package com.example.cafe_con_huellas.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado del envío de correos electrónicos automáticos del sistema.
 * <p>
 * Centraliza todas las comunicaciones por email: notificaciones al administrador,
 * confirmaciones a usuarios y envío de formularios de adopción con token único.
 * La configuración del remitente y el email del admin se leen desde {@code application.properties}.
 * </p>
 */
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

    /**
     * Método genérico para enviar un correo electrónico simple con soporte UTF-8.
     * <p>
     * Utiliza {@link MimeMessage} en lugar de {@link SimpleMailMessage} para garantizar
     * la correcta codificación de caracteres especiales como tildes y eñes.
     * Todos los emails de la aplicación pasan por este método.
     * </p>
     *
     * @param to      dirección de correo del destinatario
     * @param subject asunto del mensaje
     * @param body    cuerpo del mensaje en texto plano
     * @throws RuntimeException si ocurre un error al construir o enviar el mensaje
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            // Remitente
            helper.setFrom(fromEmail);
            // Destinatario
            helper.setTo(to);
            // Asunto
            helper.setSubject(subject);
            // Cuerpo del mensaje
            helper.setText(body, false);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email", e);
        }
    }

    /**
     * Notifica al administrador cuando un usuario envía una solicitud de adopción.
     *
     * @param userName  nombre completo del usuario solicitante
     * @param userEmail email de contacto del usuario
     * @param petName   nombre de la mascota solicitada
     */

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

    /**
     * Envía un email de confirmación al usuario cuando su solicitud de adopción ha sido registrada.
     *
     * @param userEmail email del usuario
     * @param userName  nombre del usuario para personalizar el mensaje
     * @param petName   nombre de la mascota sobre la que se ha solicitado la adopción
     */
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

    /**
     * Envía al usuario el enlace único con el token para acceder al formulario de adopción.
     * <p>
     * El enlace expira en 48 horas y es de uso único.
     * </p>
     *
     * @param userEmail email del usuario destinatario
     * @param userName  nombre del usuario para personalizar el mensaje
     * @param petName   nombre de la mascota del proceso de adopción
     * @param token     token UUID único que se incluye en el enlace del formulario
     */
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

    /**
     * Construye la URL completa del formulario público de adopción con el token único.
     * <p>
     * En producción la URL base se obtiene de {@code app.frontend.url} en
     * {@code application.properties}.
     * </p>
     *
     * @param token token UUID único que se incluye en la URL
     * @return URL completa del formulario para incluir en el email
     */
    private String buildFormLink(String token) {
        // En producción esto vendría de application.properties con la URL real del frontend
        return "http://localhost:4200/adopcion/formulario/" + token;
    }
}