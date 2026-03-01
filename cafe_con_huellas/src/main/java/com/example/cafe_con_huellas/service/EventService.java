package com.example.cafe_con_huellas.service;
import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.EventMapper;
import com.example.cafe_con_huellas.model.entity.Event;
import com.example.cafe_con_huellas.model.entity.EventStatus;
import com.example.cafe_con_huellas.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Servicio encargado de la lógica de negocio para la gestión de eventos
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    // ---------- CRUD BÁSICO ----------

    // Obtiene todos los eventos convertidos a DTO
    @Transactional(readOnly = true)
    public List<EventDTO> findAll() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    // Busca un evento por ID y lo devuelve como DTO
    @Transactional(readOnly = true)
    public EventDTO findById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));
    }

    // Registra o actualiza un evento recibiendo un DTO
    @Transactional
    public EventDTO save(EventDTO eventDTO) {
        // Convertimos el DTO a Entidad mediante el Mapper
        Event event = eventMapper.toEntity(eventDTO);

        // Guardamos en la base de datos
        Event savedEvent = eventRepository.save(event);

        // Retornamos el DTO del evento guardado
        return eventMapper.toDto(savedEvent);
    }

    // Elimina un evento por su identificador
    @Transactional
    public void deleteById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Evento no encontrado");
        }
        eventRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    // Obtiene solo los eventos que están por venir (fecha posterior a la actual)
    @Transactional(readOnly = true)
    public List<EventDTO> findUpcomingEvents() {
        return eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now())
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    // Filtra eventos por su estado (PROGRAMADO, EN_CURSO, etc.)
    @Transactional(readOnly = true)
    public List<EventDTO> findByStatus(String statusName) {
        try {
            EventStatus status = EventStatus.valueOf(statusName.toUpperCase());
            return eventRepository.findByStatus(status).stream()
                    .map(eventMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new com.example.cafe_con_huellas.exception.BadRequestException("Estado de evento no válido: " + statusName);
        }
    }
}