package com.example.cafe_con_huellas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
// http://localhost:8080/swagger-ui/index.html
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactivamos CSRF para poder probar POST/PUT en Swagger
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Permitimos Swagger
                        .anyRequest().permitAll() //  PERMITIMOS TODO TEMPORALMENTE PARA TESTEAR CON SWAGGER
                );

        return http.build();
    }
}