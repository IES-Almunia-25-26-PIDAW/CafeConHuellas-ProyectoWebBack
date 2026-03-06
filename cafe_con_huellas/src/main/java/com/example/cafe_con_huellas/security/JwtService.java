package com.example.cafe_con_huellas.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Servicio encargado de toda la lógica JWT: crear, leer y validar tokens
@Service
public class JwtService {

    // Clave secreta para firmar los tokens (mínimo 256 bits para HS256)
    // En producción esto iría en application.properties o variable de entorno
    private static final String SECRET_KEY = "cafe_con_huellas_clave_secreta_super_segura_2024";

    // Tiempo de expiración del token: 24 horas en milisegundos
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Genera un token JWT para el usuario recibido
    public String generateToken(String email) {
        return generateToken(new HashMap<>(), email);
    }

    // Genera un token con claims extra (por ejemplo el rol del usuario)
    public String generateToken(Map<String, Object> extraClaims, String email) {
        return Jwts.builder()
                // Añadimos datos extra como el rol
                .setClaims(extraClaims)
                // El "subject" es el identificador principal, usamos el email
                .setSubject(email)
                // Fecha de creación del token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Fecha de expiración
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // Firmamos con nuestra clave secreta
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrae el email (subject) del token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Comprueba si el token es válido para el email recibido
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        // Válido si el email coincide y el token no ha expirado
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    // Comprueba si el token ha caducado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extrae la fecha de expiración del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Método genérico para extraer cualquier claim del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Decodifica y devuelve todos los claims del token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                // Verificamos la firma con nuestra clave secreta
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Convierte la clave secreta en un objeto Key válido para JWT
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}