package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la gestión de la galería de imágenes de las mascotas.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * </p>
 */
@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Long> {

    /**
     * Devuelve todas las imágenes asociadas a una mascota específica.
     *
     * @param petId identificador de la mascota
     * @return lista de imágenes de la mascota indicada
     */
    List<PetImage> findByPetId(Long petId);

    /**
     * Elimina todas las imágenes asociadas a una mascota de forma masiva.
     *
     * @param petId identificador de la mascota cuyas imágenes se eliminarán
     */
    void deleteByPetId(Long petId);

}
