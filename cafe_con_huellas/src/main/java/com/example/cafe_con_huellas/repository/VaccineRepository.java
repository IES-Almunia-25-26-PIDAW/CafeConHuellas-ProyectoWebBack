package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio para gestionar los datos maestros de las vacunas disponibles
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface VaccineRepository extends JpaRepository<Vaccine,Long> {

    // Busca una vacuna por su nombre
    Optional<Vaccine> findByName(String name);

    // Comprueba si una vacuna ya existe por nombre
    boolean existsByName(String name);


}
