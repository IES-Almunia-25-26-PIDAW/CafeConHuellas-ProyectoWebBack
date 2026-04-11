package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.EventMapper;
import com.example.cafe_con_huellas.model.entity.Event;
import com.example.cafe_con_huellas.model.entity.EventStatus;
import com.example.cafe_con_huellas.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private EventDTO testEventDTO;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setName("Jornada de adopción");
        testEvent.setEventDate(LocalDateTime.now().plusDays(7));
        testEvent.setStatus(EventStatus.PROGRAMADO);

        testEventDTO = new EventDTO();
        testEventDTO.setId(1L);
        testEventDTO.setName("Jornada de adopción");
        testEventDTO.setStatus("PROGRAMADO");
    }

    @Test
    @DisplayName("Debe devolver todos los eventos correctamente")
    void shouldFindAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(testEvent));
        when(eventMapper.toDto(testEvent)).thenReturn(testEventDTO);

        List<EventDTO> result = eventService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jornada de adopción");
    }

    @Test
    @DisplayName("Debe encontrar un evento por su ID")
    void shouldFindEventById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventMapper.toDto(testEvent)).thenReturn(testEventDTO);

        EventDTO result = eventService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jornada de adopción");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el evento no existe")
    void shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.findById(99L));
    }

    @Test
    @DisplayName("Debe guardar un evento nuevo correctamente")
    void shouldSaveNewEvent() {
        // Sin ID porque es nuevo
        testEventDTO.setId(null);
        when(eventMapper.toEntity(testEventDTO)).thenReturn(testEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventMapper.toDto(testEvent)).thenReturn(testEventDTO);

        EventDTO result = eventService.save(testEventDTO);

        assertThat(result).isNotNull();
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Debe actualizar un evento existente correctamente")
    void shouldUpdateExistingEvent() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(eventMapper.toEntity(testEventDTO)).thenReturn(testEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventMapper.toDto(testEvent)).thenReturn(testEventDTO);

        EventDTO result = eventService.save(testEventDTO);

        assertThat(result).isNotNull();
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un evento que no existe")
    void shouldThrowExceptionWhenUpdatingNonExistentEvent() {
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventService.save(testEventDTO));

        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar un evento existente correctamente")
    void shouldDeleteEventById() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> eventService.deleteById(1L));

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un evento que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentEvent() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteById(99L));

        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe devolver los próximos eventos ordenados por fecha")
    void shouldFindUpcomingEvents() {
        when(eventRepository.findByEventDateAfterOrderByEventDateAsc(any(LocalDateTime.class)))
                .thenReturn(List.of(testEvent));
        when(eventMapper.toDto(testEvent)).thenReturn(testEventDTO);

        List<EventDTO> result = eventService.findUpcomingEvents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jornada de adopción");
    }

    @Test
    @DisplayName("Debe filtrar eventos por estado válido")
    void shouldFindEventsByStatus() {
        when(eventRepository.findByStatus(EventStatus.PROGRAMADO))
                .thenReturn(List.of(testEvent));
        when(eventMapper.toDto(testEvent)).thenReturn(testEventDTO);

        List<EventDTO> result = eventService.findByStatus("PROGRAMADO");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PROGRAMADO");
    }

    @Test
    @DisplayName("Debe lanzar excepción con un estado de evento no válido")
    void shouldThrowExceptionForInvalidStatus() {
        assertThrows(BadRequestException.class,
                () -> eventService.findByStatus("ESTADO_INEXISTENTE"));
    }
}