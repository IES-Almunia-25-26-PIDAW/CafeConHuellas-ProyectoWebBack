package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AuthResponseDTO;
import com.example.cafe_con_huellas.dto.LoginDTO;
import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.UserRepository;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador REST para la autenticación de usuarios.
 * <p>
 * Gestiona el acceso al sistema mediante tres endpoints públicos:
 * <ul>
 *   <li>{@code POST /api/auth/login} — autentica al usuario y devuelve ambos tokens.</li>
 *   <li>{@code POST /api/auth/refresh} — renueva el access token usando el refresh token.</li>
 *   <li>{@code POST /api/auth/register} — registro público de nuevos usuarios.</li>
 * </ul>
 * Todos los endpoints son de acceso libre (configurado en {@code SecurityConfig}).
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Autentica a un usuario y devuelve un access token y un refresh token.
     * <p>
     * Verifica las credenciales contra la base de datos usando BCrypt.
     * Si son correctas, genera ambos tokens. El email y el rol del usuario
     * viajan de forma segura <b>dentro</b> del access token como claims firmados,
     * nunca expuestos directamente en la respuesta.
     * </p>
     *
     * @param loginDTO objeto con email y contraseña del usuario
     * @return {@link AuthResponseDTO} con el access token y el refresh token
     */
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginDTO loginDTO) {

        // Spring verifica que el email existe y que la contraseña coincide con el hash BCrypt.
        // Si algo falla lanza una excepción automáticamente (capturada por el GlobalExceptionHandler)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        // Si llegamos aquí las credenciales son correctas, buscamos el usuario completo
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Generamos el access token con email (subject) y rol (claim interno)
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        // Generamos el refresh token, solo contiene el email
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }


    /**
     * Renueva el access token a partir de un refresh token válido.
     * <p>
     * El frontend debe llamar a este endpoint cuando recibe un {@code 401 Unauthorized}
     * por token expirado, enviando el refresh token en el header {@code Authorization}.
     * Si el refresh token también ha expirado, el usuario deberá volver a loguearse.
     * </p>
     *
     * @param authHeader header {@code Authorization} con formato {@code Bearer <refreshToken>}
     * @return {@link AuthResponseDTO} con el nuevo access token y el mismo refresh token
     * @throws BadRequestException si el header no tiene el formato correcto,
     *                             el token es inválido o ha expirado — devuelve {@code 400 Bad Request}
     * @throws ResourceNotFoundException si el email del token no corresponde a ningún usuario
     *                                   registrado — devuelve {@code 404 Not Found}
     */
    @PostMapping("/refresh")
    public AuthResponseDTO refresh(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Refresh token no proporcionado");
        }

        String refreshToken = authHeader.substring(7);
        String email = jwtService.extractEmail(refreshToken);

        if (email == null) {
            throw new BadRequestException("Refresh token inválido");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(refreshToken, email)) {
            throw new BadRequestException("Refresh token expirado o inválido");
        }

        String newToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponseDTO.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Endpoint público disponible para cualquier persona.
     * Delega la validación y persistencia al servicio de usuarios.
     * </p>
     *
     * @param registerDTO datos del nuevo usuario a registrar
     * @return {@link UserDetailDTO} con la información del usuario creado
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailDTO register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }
}