package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Event;
import com.example.cafe_con_huellas.model.entity.EventStatus;
import com.example.cafe_con_huellas.model.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Repositorio para gestionar el almacenamiento y consulta de eventos.
 * Proporciona métodos para filtrar eventos por su estado, tipo y fecha.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Busca eventos filtrando por su estado (ej: solo los PROGRAMADOS)
    List<Event> findByStatus(EventStatus status);

    // Busca eventos por su categoría (ej: solo jornadas de ADOPCION)
    List<Event> findByEventType(EventType eventType);

    // Busca eventos que ocurran en una ubicación específica
    List<Event> findByLocationContainingIgnoreCase(String location);

    // Busca eventos programados para después de una fecha concreta
    // Útil para mostrar "Próximos Eventos" en la web
    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime date);

    // Busca eventos de un tipo específico que aún no han pasado
    List<Event> findByEventTypeAndEventDateAfter(EventType type, LocalDateTime date);
}
