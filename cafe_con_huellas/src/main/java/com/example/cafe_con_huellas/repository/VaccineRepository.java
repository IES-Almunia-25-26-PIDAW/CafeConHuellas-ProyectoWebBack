package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para gestionar los datos maestros de las vacunas disponibles
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface VaccineRepository extends JpaRepository<Vaccine,Long> {
}
