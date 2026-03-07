package com.example.cafe_con_huellas.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

// Cargamos el contexto completo de Spring para testear el servicio JWT
@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    // Email de prueba que usaremos en todos los tests
    private String testEmail;

    // Se ejecuta antes de cada test para inicializar los datos de prueba
    @BeforeEach
    void setUp() {
        testEmail = "test@cafeconhuellas.com";
    }

    @Test
    @DisplayName("Debe generar un token JWT no vacío para un email válido")
    void shouldGenerateToken() {
        // Generamos el token
        String token = jwtService.generateToken(testEmail);

        // Verificamos que el token no es nulo ni vacío
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        // Un token JWT siempre tiene 3 partes separadas por puntos
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Debe extraer correctamente el email del token generado")
    void shouldExtractEmailFromToken() {
        // Generamos un token con nuestro email de prueba
        String token = jwtService.generateToken(testEmail);

        // Extraemos el email del token
        String extractedEmail = jwtService.extractEmail(token);

        // Verificamos que el email extraído coincide con el original
        assertThat(extractedEmail).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("Debe validar correctamente un token válido para el email correcto")
    void shouldValidateTokenForCorrectEmail() {
        // Generamos el token
        String token = jwtService.generateToken(testEmail);

        // Verificamos que el token es válido para ese email
        boolean isValid = jwtService.isTokenValid(token, testEmail);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar un token usado con un email diferente")
    void shouldRejectTokenForWrongEmail() {
        // Generamos token para un email
        String token = jwtService.generateToken(testEmail);

        // Intentamos validarlo con un email diferente
        boolean isValid = jwtService.isTokenValid(token, "otro@email.com");

        // Debe ser inválido porque el email no coincide
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Debe generar tokens diferentes para emails diferentes")
    void shouldGenerateDifferentTokensForDifferentEmails() {
        // Generamos tokens para dos emails distintos
        String token1 = jwtService.generateToken(testEmail);
        String token2 = jwtService.generateToken("otro@email.com");

        // Los tokens deben ser diferentes
        assertThat(token1).isNotEqualTo(token2);
    }
}