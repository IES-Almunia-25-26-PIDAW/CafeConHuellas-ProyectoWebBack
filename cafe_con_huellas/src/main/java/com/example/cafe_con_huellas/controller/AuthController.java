package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AuthResponseDTO;
import com.example.cafe_con_huellas.dto.LoginDTO;
import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
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

// Controlador para autenticación: login y registro público
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;

    // Endpoint de login: recibe email y contraseña, devuelve token JWT
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginDTO loginDTO) {

        // Spring verifica que el email existe y que la contraseña coincide con el hash BCrypt
        // Si algo falla lanza una excepción automáticamente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        // Si llegamos aquí las credenciales son correctas, buscamos el usuario
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow();

        // Generamos el token JWT con el email como identificador
        String token = jwtService.generateToken(loginDTO.getEmail());

        // Devolvemos el token junto con los datos básicos del usuario
        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // Endpoint de registro público: cualquiera puede crear una cuenta
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailDTO register(@Valid @RequestBody RegisterDTO registerDTO) {
        // Delegamos toda la lógica al UserService que ya tenemos
        return userService.register(registerDTO);
    }
}