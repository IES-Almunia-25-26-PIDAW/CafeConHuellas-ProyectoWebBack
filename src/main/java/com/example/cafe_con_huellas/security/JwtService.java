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
 * Gestiona la generación, validación y extracción de información de los tokens.
 * Distingue entre dos tipos de token:
 * <ul>
 *   <li><b>Access token:</b> vida corta (15 min), contiene email y rol del usuario.</li>
 *   <li><b>Refresh token:</b> vida larga (7 días), contiene solo el email y sirve
 *       únicamente para renovar el access token sin forzar un nuevo login.</li>
 * </ul>
 * La clave secreta y los tiempos de expiración se configuran en
 * {@code application.properties} mediante las propiedades
 * {@code app.jwt.secret}, {@code app.jwt.expiration} y
 * {@code app.jwt.refresh-expiration}.
 */
@Service
public class JwtService {

    /**
     * Clave secreta para firmar los tokens (mínimo 256 bits para HS256).
     * Se lee desde {@code app.jwt.secret} en application.properties.
     */
    @Value("${app.jwt.secret}")
    private String secretKey;

    /**
     * Tiempo de expiración del access token en milisegundos (15 minutos).
     * Se lee desde {@code app.jwt.expiration} en application.properties.
     */
    @Value("${app.jwt.expiration}")
    private long expirationTime;

    /**
     * Tiempo de expiración del refresh token en milisegundos (7 días).
     * Se lee desde {@code app.jwt.refresh-expiration} en application.properties.
     */
    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationTime;

    /**
     * Genera un access token JWT con el email como subject y el rol como claim.
     * <p>
     * El rol se incluye dentro del token para que el backend pueda
     * autorizarlo sin consultar la base de datos en cada petición.
     * </p>
     *
     * @param email email del usuario, usado como subject del token
     * @param role  rol del usuario (ej: "ADMIN", "USER"), incluido como claim
     * @return access token JWT firmado con HS256
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return buildToken(claims, email, expirationTime);
    }

    /**
     * Genera un refresh token JWT para el email indicado.
     * <p>
     * El refresh token no contiene rol ni otros datos sensibles.
     * Su único propósito es permitir la renovación del access token
     * cuando este expira, sin obligar al usuario a volver a loguearse.
     * </p>
     *
     * @param email email del usuario, usado como subject del token
     * @return refresh token JWT firmado con HS256
     */
    public String generateRefreshToken(String email) {
        return buildToken(new HashMap<>(), email, refreshExpirationTime);
    }

    /**
     * Método interno que construye cualquier token JWT con los parámetros indicados.
     * <p>
     * Centraliza la lógica de construcción para evitar duplicidad entre
     * {@link #generateToken(String, String)} y {@link #generateRefreshToken(String)}.
     * </p>
     *
     * @param claims    mapa de claims adicionales a incluir en el token
     * @param email     email del usuario como subject
     * @param expiration tiempo de vida del token en milisegundos
     * @return token JWT firmado y compacto
     */
    private String buildToken(Map<String, Object> claims, String email, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
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
     * Extrae el rol almacenado como claim dentro del access token.
     * <p>
     * Solo disponible en access tokens. Los refresh tokens no contienen rol.
     * </p>
     *
     * @param token access token JWT del que extraer el rol
     * @return rol del usuario (ej: "ADMIN", "USER"), o {@code null} si no existe el claim
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Comprueba si un token JWT es válido para el email indicado.
     * <p>
     * El token es válido si el email coincide con el subject
     * y el token no ha expirado. Válido tanto para access como refresh tokens.
     * </p>
     *
     * @param token token JWT a validar
     * @param email email esperado en el subject del token
     * @return {@code true} si el token es válido, {@code false} en caso contrario
     */
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
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
     * Si la firma no es válida o el token está malformado, lanza una excepción.
     * </p>
     *
     * @param token token JWT a decodificar
     * @return objeto {@link Claims} con todos los datos del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
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
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}