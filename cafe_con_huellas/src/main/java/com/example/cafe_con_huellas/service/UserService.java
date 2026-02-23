package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import com.example.cafe_con_huellas.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio encargado de la lógica de negocio relacionada con los usuarios
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ---------- CRUD BÁSICO ----------

    // Devuelve todos los usuarios registrados
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Busca un usuario por su ID
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

   /* // Guarda un nuevo usuario o actualiza uno existente
    public User save(User user) {
        return userRepository.save(user);
    }
    */

    // Registro con validación de email duplicado
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("El email " + user.getEmail() + " ya está registrado.");
        }
        // Aquí en el futuro aplicaramos passwordEncoder.encode(user.getPassword())
        return userRepository.save(user);
    }

    // Actualizar perfil (evita sobrescribir todo el objeto)
    public User updateProfile(Long id, User userDetails) {
        User user = findById(id);

        user.setFirstName(userDetails.getFirstName());
        user.setLastName1(userDetails.getLastName1());
        user.setLastName2(userDetails.getLastName2());
        user.setPhone(userDetails.getPhone());
        user.setImageUrl(userDetails.getImageUrl());

        return userRepository.save(user);
    }


    // Elimina un usuario por su ID
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Busca un usuario por su email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    // Comprueba si un email ya está registrado
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Devuelve todos los usuarios con un rol específico (ADMIN o USER)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // Buscar por nombre para filtros en la web
    public List<User> searchByName(String term) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastName1ContainingIgnoreCase(term, term);
    }
//_______________________________________________________________________________________________________________________________
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = findById(id);

        // Aquí, antes de guardar, es donde en el futuro usarás BCrypt
        // String encodedPassword = passwordEncoder.encode(newPassword);
        // user.setPassword(encodedPassword);

        user.setPassword(newPassword); // Por ahora lo guardamos así hasta que configures Security
        userRepository.save(user);
    }
  //_______________________________________________________________________________________________________________________________

    // ---------- ESTADÍSTICAS ----------

    public long getTotalUsersCount() {
        return userRepository.count();
    }


}
