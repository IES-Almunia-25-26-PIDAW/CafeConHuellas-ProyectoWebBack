package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la gestión del catálogo de vacunas disponibles.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * </p>
 */
@Repository
public interface VaccineRepository extends JpaRepository<Vaccine,Long> {

    /**
     * Busca una vacuna por su nombre exacto en el catálogo.
     *
     * @param name nombre de la vacuna a buscar
     * @return {@link Optional} con la vacuna si existe
     */
    Optional<Vaccine> findByName(String name);

    /**
     * Comprueba si ya existe una vacuna con el nombre indicado.
     * Usado para evitar duplicados en el catálogo.
     *
     * @param name nombre de la vacuna a comprobar
     * @return {@code true} si ya existe una vacuna con ese nombre
     */
    boolean existsByName(String name);


}
