package com.example.cafe_con_huellas.security;


import com.example.cafe_con_huellas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementación de UserDetailsService que Spring Security necesita obligatoriamente
// Le dice a Spring cómo buscar un usuario en nuestra BD para autenticarlo
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring llama a este método automáticamente cuando necesita autenticar a alguien
    // El "username" en nuestro caso es el email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Buscamos el usuario por email en nuestra BD
        com.example.cafe_con_huellas.model.entity.User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Convertimos nuestro Role (ADMIN/USER) al formato que entiende Spring Security
        // Spring espera que los roles tengan el prefijo "ROLE_"
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // Devolvemos el objeto UserDetails que Spring Security usará internamente
        // Contiene: email, contraseña encriptada y lista de permisos
        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
