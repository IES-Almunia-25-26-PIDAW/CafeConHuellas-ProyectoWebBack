package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Donation;
import com.example.cafe_con_huellas.model.entity.DonationCategory;
import com.example.cafe_con_huellas.model.entity.DonationMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio para gestionar el almacenamiento y consulta de donaciones
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> {

    // Devuelve todas las donaciones realizadas por un usuario
    List<Donation> findByUserId(Long userId);

    // Devuelve donaciones filtradas por categoría
    List<Donation> findByCategory(DonationCategory category);

    // Devuelve donaciones filtradas por método
    List<Donation> findByMethod(DonationMethod method);

    // Suma total donada por un usuario
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.user.id = :userId")
    Double sumAmountByUserId(Long userId);

    // Suma total de todas las donaciones
    @Query("SELECT SUM(d.amount) FROM Donation d")
    Double sumTotalAmount();

    // Buscar todas las donaciones anónimas donde user_id es null
    List<Donation> findByUserIsNull();

}
