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

    private String testEmail;
    private String testRole;

    // Se ejecuta antes de cada test para inicializar los datos de prueba
    @BeforeEach
    void setUp() {
        testEmail = "test@cafeconhuellas.com";
        testRole  = "USER";
    }

    // -------------------- ACCESS TOKEN --------------------

    @Test
    @DisplayName("Debe generar un access token JWT no vacío para email y rol válidos")
    void shouldGenerateAccessToken() {
        String token = jwtService.generateToken(testEmail, testRole);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        // Un token JWT siempre tiene 3 partes separadas por puntos
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Debe extraer correctamente el email del access token generado")
    void shouldExtractEmailFromAccessToken() {
        String token = jwtService.generateToken(testEmail, testRole);

        String extractedEmail = jwtService.extractEmail(token);

        assertThat(extractedEmail).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("Debe extraer correctamente el rol del access token generado")
    void shouldExtractRoleFromAccessToken() {
        String token = jwtService.generateToken(testEmail, testRole);

        String extractedRole = jwtService.extractRole(token);

        assertThat(extractedRole).isEqualTo(testRole);
    }

    @Test
    @DisplayName("Debe validar correctamente un access token válido para el email correcto")
    void shouldValidateAccessTokenForCorrectEmail() {
        String token = jwtService.generateToken(testEmail, testRole);

        boolean isValid = jwtService.isTokenValid(token, testEmail);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar un access token usado con un email diferente")
    void shouldRejectAccessTokenForWrongEmail() {
        String token = jwtService.generateToken(testEmail, testRole);

        boolean isValid = jwtService.isTokenValid(token, "otro@test.com");

        assertThat(isValid).isFalse();
    }

    // -------------------- REFRESH TOKEN --------------------

    @Test
    @DisplayName("Debe generar un refresh token JWT no vacío para un email válido")
    void shouldGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testEmail);

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Debe extraer correctamente el email del refresh token generado")
    void shouldExtractEmailFromRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testEmail);

        String extractedEmail = jwtService.extractEmail(refreshToken);

        assertThat(extractedEmail).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("El refresh token no debe contener rol")
    void shouldNotContainRoleInRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testEmail);

        // El refresh token no lleva rol, debe devolver null
        String extractedRole = jwtService.extractRole(refreshToken);

        assertThat(extractedRole).isNull();
    }

    @Test
    @DisplayName("Debe validar correctamente un refresh token válido para el email correcto")
    void shouldValidateRefreshTokenForCorrectEmail() {
        String refreshToken = jwtService.generateRefreshToken(testEmail);

        boolean isValid = jwtService.isTokenValid(refreshToken, testEmail);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Access token y refresh token deben ser distintos entre sí")
    void accessTokenAndRefreshTokenShouldBeDifferent() {
        String accessToken  = jwtService.generateToken(testEmail, testRole);
        String refreshToken = jwtService.generateRefreshToken(testEmail);

        assertThat(accessToken).isNotEqualTo(refreshToken);
    }
}