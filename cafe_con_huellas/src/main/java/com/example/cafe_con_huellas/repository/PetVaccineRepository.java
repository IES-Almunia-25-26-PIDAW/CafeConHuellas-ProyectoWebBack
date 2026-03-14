package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.PetVaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para el seguimiento del calendario de vacunación de las mascotas.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * Proporciona filtros por mascota, tipo de vacuna y fechas de próxima dosis.
 * </p>
 */
@Repository
public interface PetVaccineRepository extends JpaRepository<PetVaccine, Long> {

    /**
     * Devuelve el historial completo de vacunación de una mascota específica.
     *
     * @param petId identificador de la mascota
     * @return lista de registros de vacunación de la mascota indicada
     */
    List<PetVaccine> findByPetId(Long petId);

    /**
     * Devuelve todos los registros de un tipo de vacuna concreto.
     *
     * @param vaccineId identificador del tipo de vacuna
     * @return lista de registros de ese tipo de vacuna
     */
    List<PetVaccine> findByVaccineId(Long vaccineId);

    /**
     * Devuelve los registros cuya próxima dosis está programada después de la fecha indicada.
     * Útil para identificar vacunas con refuerzo pendiente próximo.
     *
     * @param date fecha de referencia
     * @return lista de registros con próxima dosis posterior a la fecha indicada
     */
    List<PetVaccine> findByNextDoseDateAfter(LocalDate date);

    /**
     * Devuelve los registros cuya próxima dosis estaba programada antes de la fecha indicada.
     * Útil para identificar vacunas vencidas pendientes de aplicar.
     *
     * @param date fecha de referencia
     * @return lista de registros con próxima dosis anterior a la fecha indicada
     */
    List<PetVaccine> findByNextDoseDateBefore(LocalDate date);




}
