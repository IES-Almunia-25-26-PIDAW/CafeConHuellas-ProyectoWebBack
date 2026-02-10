package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio para gestionar las imágenes adicionales de la galería de cada mascota
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
*/
@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Long> {

    // Devuelve todas las imágenes asociadas a una mascota
    List<PetImage> findByPetId(Long petId);

    // Elimina todas las imágenes asociadas a una mascota
    void deleteByPetId(Long petId);

}
