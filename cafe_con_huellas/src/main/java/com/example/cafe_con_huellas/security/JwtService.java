package com.example.cafe_con_huellas.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio encargado de toda la lógica relacionada con los tokens JWT.
 * <p>
 * Gestiona la generación, validación y extracción de información de los tokens.
 * La clave secreta y el tiempo de expiración se configuran en
 * {@code application.properties} mediante las propiedades
 * {@code app.jwt.secret} y {@code app.jwt.expiration}.
 * </p>
 */
@Service
public class JwtService {

    // Clave secreta para firmar los tokens (mínimo 256 bits para HS256)
    // Las leemos desde application.properties
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long expirationTime;

    /**
     * Genera un token JWT para el email indicado sin claims adicionales.
     *
     * @param email email del usuario que se incluirá como subject del token
     * @return token JWT firmado y listo para usar
     */
    public String generateToken(String email) {
        return generateToken(new HashMap<>(), email);
    }

    /**
     * Genera un token JWT con claims personalizados adicionales.
     *
     * @param extraClaims mapa con los claims extra a incluir en el token (ej: rol)
     * @param email       email del usuario que se incluirá como subject del token
     * @return token JWT firmado con los claims indicados
     */
    public String generateToken(Map<String, Object> extraClaims, String email) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Usamos expirationTime desde properties
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Extrae el email (subject) almacenado dentro del token JWT.
     *
     * @param token token JWT del que extraer el email
     * @return email contenido en el subject del token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Comprueba si un token JWT es válido para el email indicado.
     * <p>
     * El token es válido si el email coincide con el subject
     * y el token no ha expirado.
     * </p>
     *
     * @param token token JWT a validar
     * @param email email esperado en el subject del token
     * @return {@code true} si el token es válido, {@code false} en caso contrario
     */
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        // Válido si el email coincide y el token no ha expirado
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    /**
     * Comprueba si la fecha de expiración del token ya ha pasado.
     *
     * @param token token JWT a comprobar
     * @return {@code true} si el token ha caducado, {@code false} si sigue vigente
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración almacenada en el token JWT.
     *
     * @param token token JWT del que extraer la fecha
     * @return fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para extraer cualquier claim del token mediante una función de resolución.
     *
     * @param <T>            tipo del valor del claim a extraer
     * @param token          token JWT del que extraer el claim
     * @param claimsResolver función que define qué claim extraer de los {@link Claims}
     * @return valor del claim solicitado
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Decodifica el token JWT y devuelve todos sus claims.
     * <p>
     * Verifica la firma del token con la clave secreta antes de devolver los datos.
     * </p>
     *
     * @param token token JWT a decodificar
     * @return objeto {@link Claims} con todos los datos del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                // Verificamos la firma con nuestra clave secreta
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Construye la clave de firma HMAC-SHA256 a partir de la clave secreta configurada.
     *
     * @return clave criptográfica lista para firmar y verificar tokens
     */
    private Key getSigningKey() {
        // Usamos secretKey desde properties
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}