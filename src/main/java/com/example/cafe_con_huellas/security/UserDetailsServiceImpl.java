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

/**
 * Implementación de {@link UserDetailsService} que conecta Spring Security con la base de datos.
 * <p>
 * Spring Security llama automáticamente a esta clase para cargar los datos
 * de un usuario durante el proceso de autenticación. Busca al usuario por
 * email en la base de datos y construye el objeto {@link UserDetails}
 * con su contraseña encriptada y su rol con el prefijo {@code ROLE_}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga los datos de un usuario a partir de su email para que Spring Security
     * pueda verificar sus credenciales y establecer sus permisos.
     * <p>
     * El rol del usuario se convierte al formato que espera Spring Security,
     * añadiendo el prefijo {@code ROLE_} (ej: {@code ROLE_ADMIN}, {@code ROLE_USER}).
     * </p>
     *
     * @param email email del usuario a cargar, usado como identificador de login
     * @return {@link UserDetails} con el email, la contraseña encriptada y los permisos
     * @throws UsernameNotFoundException si no existe ningún usuario con ese email
     */
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
