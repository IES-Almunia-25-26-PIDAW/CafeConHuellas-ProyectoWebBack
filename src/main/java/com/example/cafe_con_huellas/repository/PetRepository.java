package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA principal para el catálogo de mascotas del refugio.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * Proporciona filtros por esterilización, categoría, PPP, edad y búsqueda por texto.
 * </p>
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * Filtra las mascotas por su estado de esterilización.
     *
     * @param neutered {@code true} para esterilizadas, {@code false} para no esterilizadas
     * @return lista de mascotas con el estado de esterilización indicado
     */
    List<Pet> findByNeutered(Boolean neutered);

    /**
     * Filtra las mascotas por su categoría (PERRO o GATO).
     *
     * @param category categoría por la que filtrar ({@link PetCategory})
     * @return lista de mascotas de la categoría indicada
     */
    List<Pet> findByCategory(PetCategory category);

    /**
     * Filtra las mascotas según si están clasificadas como Perro Potencialmente Peligroso.
     *
     * @param isPpp {@code true} para PPP, {@code false} para no PPP
     * @return lista de mascotas con la clasificación PPP indicada
     */
    List<Pet> findByIsPpp(Boolean isPpp);

    /**
     * Filtra las mascotas según si su adopción es urgente o no.
     *
     * @param urgentAdoption {@code true} para adopciones urgentes
     * @return lista de mascotas con el estado de urgencia indicado
     */
    List<Pet> findByUrgentAdoption(Boolean urgentAdoption);

    /**
     * Filtra las mascotas cuya edad sea igual o inferior a la indicada.
     *
     * @param age edad máxima en años
     * @return lista de mascotas con edad menor o igual a la indicada
     */

    List<Pet> findByAgeLessThanEqual(Integer age);

    /**
     * Busca mascotas cuyo nombre o raza contengan el texto indicado,
     * ignorando mayúsculas y minúsculas.
     *
     * @param name  texto a buscar en el nombre de la mascota
     * @param breed texto a buscar en la raza de la mascota
     * @return lista de mascotas que coinciden con alguno de los criterios
     */
    List<Pet> findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(
            String name, String breed
    );





}
