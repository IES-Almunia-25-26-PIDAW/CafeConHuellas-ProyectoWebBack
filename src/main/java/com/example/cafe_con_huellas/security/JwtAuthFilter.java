package com.example.cafe_con_huellas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad que intercepta cada petición HTTP para validar el token JWT.
 * <p>
 * Se ejecuta una única vez por petición gracias a {@link OncePerRequestFilter}.
 * Lee el token del header {@code Authorization}, extrae el email, valida
 * el token y, si es correcto, establece la autenticación en el contexto
 * de seguridad de Spring para que el resto de filtros y controladores
 * reconozcan al usuario.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Procesa el token JWT de cada petición entrante.
     * <p>
     * Si el header {@code Authorization} no está presente o no tiene el formato
     * {@code Bearer <token>}, la petición continúa sin autenticar.
     * Si el token es válido, registra la autenticación en el
     * {@link SecurityContextHolder} para la duración de la petición.
     * </p>
     *
     * @param request     petición HTTP entrante
     * @param response    respuesta HTTP
     * @param filterChain cadena de filtros de Spring Security
     * @throws ServletException si ocurre un error en el procesamiento del filtro
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Leemos el header "Authorization" de la petición
        // Debe venir con formato: "Bearer eyJhbGciOi..."
        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza por "Bearer ", dejamos pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token quitando el prefijo "Bearer "
        final String jwt = authHeader.substring(7);

        // Extraemos el email que está dentro del token
        final String email = jwtService.extractEmail(jwt);

        // Si tenemos email y el usuario aún no está autenticado en esta petición
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargamos los datos del usuario desde la BD usando su email
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Validamos que el token sea correcto para ese usuario
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                // Creamos el objeto de autenticación con sus permisos
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Las credenciales son null porque ya validamos el token
                                userDetails.getAuthorities()
                        );

                // Añadimos detalles extra de la petición HTTP
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Guardamos la autenticación en el contexto de seguridad
                // A partir de aquí Spring sabe quién es el usuario en esta petición
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Dejamos continuar la petición al siguiente filtro o controlador
        filterChain.doFilter(request, response);
    }
}