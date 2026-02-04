package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para gestionar el almacenamiento y consulta de donaciones
/* Hereda métodos CRUD automáticos de JPA (save, findById, findAll, delete)
 * No requiere implementación manual para operaciones básicas
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> {
}
