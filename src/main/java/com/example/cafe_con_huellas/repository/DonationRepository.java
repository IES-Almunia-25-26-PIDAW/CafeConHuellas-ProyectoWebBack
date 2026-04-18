package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Donation;
import com.example.cafe_con_huellas.model.entity.DonationCategory;
import com.example.cafe_con_huellas.model.entity.DonationMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la gestión y consulta de donaciones.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * Incluye consultas JPQL para calcular totales agregados.
 * </p>
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> {

    /**
     * Devuelve todas las donaciones realizadas por un usuario concreto.
     *
     * @param userId identificador del usuario
     * @return lista de donaciones del usuario indicado
     */
    List<Donation> findByUserId(Long userId);

    /**
     * Devuelve todas las donaciones realizadas por el usuario con el email indicado.
     * <p>
     * Usado por {@code GET /api/donations/me} para que el usuario autenticado
     * consulte únicamente sus propias donaciones. El email se extrae del JWT,
     * nunca se acepta como parámetro del cliente.
     * </p>
     *
     * @param email email del usuario autenticado
     * @return lista de donaciones del usuario indicado
     */
    List<Donation> findByUserEmail(String email);

    /**
     * Filtra las donaciones por su categoría.
     *
     * @param category categoría por la que filtrar ({@link DonationCategory})
     * @return lista de donaciones de la categoría indicada
     */
    List<Donation> findByCategory(DonationCategory category);

    /**
     * Filtra las donaciones por el método de pago utilizado.
     *
     * @param method método de pago por el que filtrar ({@link DonationMethod})
     * @return lista de donaciones realizadas con ese método
     */
    List<Donation> findByMethod(DonationMethod method);

    /**
     * Calcula la suma total del importe donado por un usuario específico.
     *
     * @param userId identificador del usuario
     * @return importe total acumulado, o {@code null} si no tiene donaciones
     */
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.user.id = :userId")
    java.math.BigDecimal sumAmountByUserId(Long userId);

    /**
     * Calcula la suma total del importe de todas las donaciones del sistema.
     *
     * @return importe total global, o {@code null} si no hay donaciones
     */
    @Query("SELECT SUM(d.amount) FROM Donation d")
    java.math.BigDecimal sumTotalAmount();

    /**
     * Devuelve todas las donaciones anónimas, es decir, aquellas sin usuario asociado.
     *
     * @return lista de donaciones con {@code user} nulo
     */
    List<Donation> findByUserIsNull();

}
