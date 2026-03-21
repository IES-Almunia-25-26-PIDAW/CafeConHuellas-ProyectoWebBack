package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.AuthResponseDTO;
import com.example.cafe_con_huellas.dto.LoginDTO;
import com.example.cafe_con_huellas.dto.RegisterDTO;
import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.UserRepository;
import com.example.cafe_con_huellas.security.JwtService;
import com.example.cafe_con_huellas.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // -------------------- LOGIN --------------------

    @Test
    @DisplayName("Login exitoso devuelve access token y refresh token")
    void shouldLoginSuccessfully() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("ana@test.com")
                .password("password123")
                .build();

        User user = User.builder()
                .email("ana@test.com")
                .role(Role.USER)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("ana@test.com", "password123"));
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("ana@test.com", "USER")).thenReturn("mocked.access.token");
        when(jwtService.generateRefreshToken("ana@test.com")).thenReturn("mocked.refresh.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.access.token"))
                .andExpect(jsonPath("$.refreshToken").value("mocked.refresh.token"))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    @DisplayName("Login con credenciales incorrectas devuelve 401")
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("ana@test.com")
                .password("wrongpassword")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login con datos inválidos devuelve 400")
    void shouldReturn400WhenLoginDataInvalid() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("no-es-un-email")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());
    }

    // -------------------- REFRESH --------------------

    @Test
    @DisplayName("Refresh con token válido devuelve nuevo access token")
    void shouldRefreshTokenSuccessfully() throws Exception {
        String refreshToken = "valid.refresh.token";
        String email = "ana@test.com";

        User user = User.builder()
                .email(email)
                .role(Role.USER)
                .build();

        // Mock del filtro JWT para que no falle al procesar el token en la petición
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                email, "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.extractEmail(refreshToken)).thenReturn(email);
        when(jwtService.isTokenValid(refreshToken, email)).thenReturn(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(email, "USER")).thenReturn("new.access.token");

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new.access.token"))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("Refresh sin header Authorization devuelve 400")
    void shouldReturn400WhenNoAuthHeader() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Refresh con token expirado devuelve 400")
    void shouldReturn400WhenRefreshTokenExpired() throws Exception {
        String expiredToken = "expired.refresh.token";
        String email = "ana@test.com";

        User user = User.builder()
                .email(email)
                .role(Role.USER)
                .build();

        // Mock del filtro JWT para que no falle al procesar el token en la petición
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                email, "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.extractEmail(expiredToken)).thenReturn(email);
        when(jwtService.isTokenValid(expiredToken, email)).thenReturn(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isBadRequest());
    }

    // -------------------- REGISTER --------------------

    @Test
    @DisplayName("Registro exitoso devuelve 201 y datos del usuario")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("password123")
                .phone("612345678")
                .build();

        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                .email("ana@test.com")
                .firstName("Ana")
                .lastName1("Cruces")
                .role("USER")
                .build();

        when(userService.register(any(RegisterDTO.class))).thenReturn(userDetailDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@test.com"))
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @DisplayName("Registro con datos inválidos devuelve 400")
    void shouldReturn400WhenRegisterDataInvalid() throws Exception {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstName("")
                .lastName1("")
                .email("no-es-email")
                .password("123")
                .phone("abc")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }
}