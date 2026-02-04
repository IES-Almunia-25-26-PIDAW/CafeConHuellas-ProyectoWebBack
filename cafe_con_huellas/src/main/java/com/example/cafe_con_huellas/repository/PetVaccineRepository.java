package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.PetVaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Repositorio para el seguimiento del calendario de vacunación de cada mascota
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface PetVaccineRepository extends JpaRepository<PetVaccine, Long> {
    List<PetVaccine> findByPetId(Long petId);
    List<PetVaccine> findByVaccineId(Long vaccineId);
    List<PetVaccine> findByNextDoseDateAfter(LocalDate date);
    List<PetVaccine> findByNextDoseDateBefore(LocalDate date);




}
