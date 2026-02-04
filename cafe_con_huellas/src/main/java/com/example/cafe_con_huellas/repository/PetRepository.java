package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio principal para el catálogo de mascotas del refugio
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByNeutered(Boolean neutered);
    List<Pet> findByCategory(PetCategory category);
    List<Pet> findByIsPpp(Boolean isPpp);
    List<Pet> findByAgeLessThanEqual(Integer age);
    List<Pet> findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(
            String name, String breed
    );





}
