package com.example.cafe_con_huellas.repository;

import com.example.cafe_con_huellas.model.entity.Event;
import com.example.cafe_con_huellas.model.entity.EventStatus;
import com.example.cafe_con_huellas.model.entity.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    private LocalDateTime futureDate;
    private LocalDateTime pastDate;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        futureDate = LocalDateTime.now().plusDays(10);
        pastDate = LocalDateTime.now().minusDays(10);

        // Evento programado de adopción en el futuro
        eventRepository.save(Event.builder()
                .name("Jornada de Adopción")
                .description("Ven a conocer a nuestros animales")
                .eventDate(futureDate)
                .location("Parque Central de Sevilla")
                .eventType(EventType.ADOPCION)
                .status(EventStatus.PROGRAMADO)
                .maxCapacity(100)
                .build());

        // Evento programado de mercadillo en el futuro
        eventRepository.save(Event.builder()
                .name("Mercadillo Solidario")
                .description("Recaudación de fondos")
                .eventDate(futureDate.plusDays(5))
                .location("Plaza Mayor de Jerez")
                .eventType(EventType.MERCADILLO)
                .status(EventStatus.PROGRAMADO)
                .build());

        // Evento finalizado en el pasado
        eventRepository.save(Event.builder()
                .name("Charla Educativa")
                .description("Taller sobre cuidado animal")
                .eventDate(pastDate)
                .location("Centro Cívico Sevilla")
                .eventType(EventType.EDUCACION)
                .status(EventStatus.FINALIZADO)
                .build());
    }

    @Test
    @DisplayName("Debe encontrar eventos por estado")
    void shouldFindByStatus() {
        List<Event> result = eventRepository.findByStatus(EventStatus.PROGRAMADO);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> e.getStatus() == EventStatus.PROGRAMADO);
    }

    @Test
    @DisplayName("Debe encontrar eventos por tipo")
    void shouldFindByEventType() {
        List<Event> result = eventRepository.findByEventType(EventType.ADOPCION);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jornada de Adopción");
    }

    @Test
    @DisplayName("Debe encontrar eventos por ubicación ignorando mayúsculas")
    void shouldFindByLocationContainingIgnoreCase() {
        List<Event> result = eventRepository.findByLocationContainingIgnoreCase("sevilla");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> e.getLocation().toLowerCase().contains("sevilla"));
    }

    @Test
    @DisplayName("Debe encontrar eventos futuros ordenados por fecha")
    void shouldFindByEventDateAfterOrderByEventDateAsc() {
        List<Event> result = eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEventDate()).isBefore(result.get(1).getEventDate());
    }

    @Test
    @DisplayName("Debe encontrar eventos futuros por tipo")
    void shouldFindByEventTypeAndEventDateAfter() {
        List<Event> result = eventRepository.findByEventTypeAndEventDateAfter(
                EventType.ADOPCION, LocalDateTime.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventType()).isEqualTo(EventType.ADOPCION);
    }

    @Test
    @DisplayName("Debe devolver lista vacía si no hay eventos de ese tipo en el futuro")
    void shouldReturnEmptyWhenNoFutureEventsOfType() {
        List<Event> result = eventRepository.findByEventTypeAndEventDateAfter(
                EventType.EDUCACION, LocalDateTime.now());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe guardar evento con @PrePersist asignando createdAt")
    void shouldSaveEventWithCreatedAt() {
        Event event = Event.builder()
                .name("Nuevo Evento")
                .description("Descripción del evento")
                .eventDate(futureDate)
                .location("Madrid")
                .eventType(EventType.OTRO)
                .status(EventStatus.PROGRAMADO)
                .build();

        Event saved = eventRepository.save(event);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}