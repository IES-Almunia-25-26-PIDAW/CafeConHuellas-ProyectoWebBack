package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Event;
import com.example.cafe_con_huellas.model.entity.EventStatus;
import com.example.cafe_con_huellas.model.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la gestión y consulta de eventos del refugio.
 * <p>
 * Hereda los métodos CRUD básicos de {@link JpaRepository}.
 * Proporciona filtros por estado, tipo, ubicación y fecha.
 * </p>
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Filtra los eventos por su estado actual.
     *
     * @param status estado por el que filtrar ({@link EventStatus})
     * @return lista de eventos con el estado indicado
     */
    List<Event> findByStatus(EventStatus status);

    /**
     * Filtra los eventos por su tipo o categoría.
     *
     * @param eventType tipo de evento por el que filtrar ({@link EventType})
     * @return lista de eventos del tipo indicado
     */
    List<Event> findByEventType(EventType eventType);

    /**
     * Busca eventos cuya ubicación contenga el texto indicado, ignorando mayúsculas.
     *
     * @param location texto a buscar en el campo de ubicación
     * @return lista de eventos que coinciden con la ubicación
     */
    List<Event> findByLocationContainingIgnoreCase(String location);

    /**
     * Devuelve los eventos programados para después de una fecha concreta,
     * ordenados cronológicamente de más próximo a más lejano.
     * Usado para mostrar la sección de próximos eventos.
     *
     * @param date fecha a partir de la cual buscar eventos
     * @return lista de eventos futuros ordenados por fecha ascendente
     */
    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime date);

    /**
     * Busca eventos de un tipo específico que aún no han tenido lugar.
     *
     * @param type tipo de evento ({@link EventType})
     * @param date fecha de referencia; se devuelven eventos posteriores a ella
     * @return lista de eventos futuros del tipo indicado
     */
    List<Event> findByEventTypeAndEventDateAfter(EventType type, LocalDateTime date);
}
