package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User admin = User.builder()
                .firstName("Carlos")
                .lastName1("García")
                .email("admin@test.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();

        User user1 = User.builder()
                .firstName("Ana")
                .lastName1("Cruces")
                .email("ana@test.com")
                .password("password123")
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .firstName("Pedro")
                .lastName1("Martínez")
                .email("pedro@test.com")
                .password("password123")
                .role(Role.USER)
                .build();

        userRepository.saveAll(List.of(admin, user1, user2));
    }

    @Test
    @DisplayName("Debe encontrar un usuario por su email")
    void shouldFindByEmail() {
        Optional<User> result = userRepository.findByEmail("ana@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("Debe devolver vacío si el email no existe")
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> result = userRepository.findByEmail("noexiste@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe devolver true si el email ya está registrado")
    void shouldReturnTrueWhenEmailExists() {
        boolean exists = userRepository.existsByEmail("ana@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si el email no está registrado")
    void shouldReturnFalseWhenEmailNotExists() {
        boolean exists = userRepository.existsByEmail("noexiste@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe devolver usuarios filtrados por rol ADMIN")
    void shouldFindByRoleAdmin() {
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("Debe devolver usuarios filtrados por rol USER")
    void shouldFindByRoleUser() {
        List<User> users = userRepository.findByRole(Role.USER);

        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Debe buscar usuarios por nombre ignorando mayúsculas")
    void shouldFindByFirstNameContainingIgnoreCase() {
        List<User> result = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastName1ContainingIgnoreCase("ana", "ana");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("Debe buscar usuarios por apellido ignorando mayúsculas")
    void shouldFindByLastName1ContainingIgnoreCase() {
        List<User> result = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastName1ContainingIgnoreCase("martínez", "martínez");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName1()).isEqualTo("Martínez");
    }

    @Test
    @DisplayName("Debe devolver lista vacía si no hay coincidencias en la búsqueda")
    void shouldReturnEmptyWhenNoMatch() {
        List<User> result = userRepository
                .findByFirstNameContainingIgnoreCaseOrLastName1ContainingIgnoreCase("xyz", "xyz");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe contar correctamente los usuarios por rol")
    void shouldCountByRole() {
        long adminCount = userRepository.countByRole(Role.ADMIN);
        long userCount = userRepository.countByRole(Role.USER);

        assertThat(adminCount).isEqualTo(1);
        assertThat(userCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe guardar y recuperar un usuario correctamente")
    void shouldSaveAndFindUser() {
        User nuevo = User.builder()
                .firstName("Laura")
                .lastName1("Pérez")
                .email("laura@test.com")
                .password("password123")
                .role(Role.USER)
                .build();

        User saved = userRepository.save(nuevo);

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Debe eliminar un usuario correctamente")
    void shouldDeleteUser() {
        User user = userRepository.findByEmail("pedro@test.com").get();
        Long id = user.getId();

        userRepository.deleteById(id);

        assertThat(userRepository.findById(id)).isEmpty();
    }
}