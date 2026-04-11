package com.example.cafe_con_huellas.service;
import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.exception.BadRequestException;
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

/**
 * Servicio encargado de la lógica de negocio para la gestión de eventos del refugio.
 * <p>
 * Permite crear, consultar, actualizar y eliminar eventos,
 * así como filtrarlos por fecha futura o estado.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todos los eventos convertidos a DTO.
     *
     * @return lista de {@link EventDTO} con todos los eventos registrados
     */
    @Transactional(readOnly = true)
    public List<EventDTO> findAll() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un evento por su identificador.
     *
     * @param id identificador único del evento
     * @return {@link EventDTO} con los datos del evento
     * @throws ResourceNotFoundException si no existe el evento con ese ID
     */
    @Transactional(readOnly = true)
    public EventDTO findById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));
    }

    /**
     * Registra un nuevo evento o actualiza uno existente.
     * <p>
     * Si el DTO incluye un ID, verifica que el evento exista antes de actualizar.
     * </p>
     *
     * @param eventDTO datos del evento a persistir
     * @return {@link EventDTO} con el evento guardado
     * @throws ResourceNotFoundException si se intenta actualizar un evento inexistente
     */
    @Transactional
    public EventDTO save(EventDTO eventDTO) {
        if (eventDTO.getId() != null) {
            if (!eventRepository.existsById(eventDTO.getId())) {
                throw new ResourceNotFoundException("No se puede actualizar. Evento no encontrado con ID: " + eventDTO.getId());
            }
        }
        // Convertimos el DTO a Entidad mediante el Mapper
        Event event = eventMapper.toEntity(eventDTO);

        // Guardamos en la base de datos
        Event savedEvent = eventRepository.save(event);

        // Retornamos el DTO del evento guardado
        return eventMapper.toDto(savedEvent);
    }

    /**
     * Elimina un evento del sistema por su identificador.
     *
     * @param id identificador del evento a eliminar
     * @throws ResourceNotFoundException si no existe el evento con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Evento no encontrado");
        }
        eventRepository.deleteById(id);
    }

    // ---------- MÉTODOS ESPECÍFICOS ----------

    /**
     * Obtiene los eventos cuya fecha es posterior a la actual, ordenados cronológicamente.
     *
     * @return lista de {@link EventDTO} con los próximos eventos
     */
    @Transactional(readOnly = true)
    public List<EventDTO> findUpcomingEvents() {
        return eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now())
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Filtra los eventos por su estado.
     * <p>
     * Convierte el {@code String} recibido al enum {@link EventStatus}.
     * </p>
     *
     * @param statusName nombre del estado en texto (insensible a mayúsculas)
     * @return lista de {@link EventDTO} que coinciden con el estado indicado
     * @throws BadRequestException si el estado no es válido
     */
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