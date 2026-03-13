package com.example.cafe_con_huellas.config;

import com.example.cafe_con_huellas.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuración central de Spring Security para la aplicación.
 * <p>
 * Define la cadena de filtros, las rutas públicas y protegidas,
 * la política de sesiones sin estado (JWT), el proveedor de autenticación
 * y habilita la seguridad a nivel de método con {@code @PreAuthorize}.
 * </p>
 */
// Activamos la seguridad a nivel de método para poder usar @PreAuthorize
@EnableMethodSecurity
// Configuración central de Spring Security
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Bean global para encriptar y verificar contraseñas usando el algoritmo BCrypt.
     *
     * @return instancia de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación que combina la carga de usuarios desde la base de datos
     * con la verificación de contraseñas mediante BCrypt.
     *
     * @return {@link AuthenticationProvider} configurado con el servicio de usuarios y el encoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Le decimos cómo cargar usuarios (por email desde nuestra BD)
        provider.setUserDetailsService(userDetailsService);
        // Le decimos cómo verificar contraseñas (BCrypt)
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expone el {@link AuthenticationManager} como bean de Spring para poder
     * inyectarlo en el {@code AuthController} durante el proceso de login.
     *
     * @param config configuración de autenticación de Spring
     * @return instancia del {@link AuthenticationManager}
     * @throws Exception si ocurre algún error al obtener el manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * Define la cadena de filtros de seguridad HTTP de la aplicación.
     * <p>
     * Configura los siguientes aspectos:
     * <ul>
     *   <li>CSRF desactivado al usar JWT en lugar de sesiones.</li>
     *   <li>Política de sesiones sin estado: cada petición debe incluir su propio token.</li>
     *   <li>Rutas públicas: {@code /api/auth/**}, formulario de adopción y documentación Swagger.</li>
     *   <li>El resto de rutas requieren autenticación.</li>
     *   <li>El filtro {@link JwtAuthFilter} se ejecuta antes del filtro estándar de Spring.</li>
     * </ul>
     * </p>
     *
     * @param http objeto de configuración de seguridad HTTP
     * @return {@link SecurityFilterChain} con la configuración aplicada
     * @throws Exception si ocurre algún error durante la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF porque usamos JWT, no sesiones
                .csrf(csrf -> csrf.disable())

                // Sin estado: cada petición debe traer su propio token
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas: login, registro, Swagger y formulario de adopción
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/adoption-form/validate/**",
                                "/api/adoption-form/submit/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // Usamos nuestro proveedor de autenticación personalizado
                .authenticationProvider(authenticationProvider())

                // Añadimos nuestro filtro JWT antes del filtro de login de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}