package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.service.EventService;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de eventos del refugio.
 * <p>
 * Expone endpoints para crear, consultar, actualizar, eliminar y filtrar
 * eventos. La consulta es pública; la creación, edición y borrado requieren rol ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Obtiene el listado completo de todos los eventos, pasados y futuros.
     *
     * @return lista de {@link EventDTO} con todos los eventos registrados
     */
    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.findAll();
    }

    /**
     * Obtiene el detalle de un evento concreto por su identificador.
     *
     * @param id identificador único del evento
     * @return {@link EventDTO} con los datos del evento
     * @throws ResourceNotFoundException si no existe evento con ese ID
     */
    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    /**
     * Registra un nuevo evento en el sistema.
     * Requiere rol ADMIN.
     *
     * @param eventDTO datos del evento a crear
     * @return {@link EventDTO} con el evento persistido
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public EventDTO createEvent(@Valid @RequestBody EventDTO eventDTO) {
        return eventService.save(eventDTO);
    }

    /**
     * Actualiza los datos de un evento existente.
     * Requiere rol ADMIN.
     *
     * @param id       identificador del evento a actualizar
     * @param eventDTO nuevos datos del evento
     * @return {@link EventDTO} con los datos actualizados
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EventDTO updateEvent(@PathVariable Long id, @Valid @RequestBody EventDTO eventDTO) {
        eventDTO.setId(id); // Nos aseguramos de actualizar el ID correcto
        return eventService.save(eventDTO);
    }

    /**
     * Elimina un evento del sistema.
     * Requiere rol ADMIN.
     *
     * @param id identificador del evento a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteById(id);
    }

    /**
     * Obtiene únicamente los eventos próximos cuya fecha aún no ha pasado.
     *
     * @return lista de {@link EventDTO} con los eventos futuros
     */
    @GetMapping("/upcoming")
    public List<EventDTO> getUpcomingEvents() {
        return eventService.findUpcomingEvents();
    }

    /**
     * Filtra los eventos por su estado actual.
     *
     * @param status estado por el que filtrar (ej: PROGRAMADO, CANCELADO)
     * @return lista de {@link EventDTO} que coinciden con el estado indicado
     */
    @GetMapping("/status/{status}")
    public List<EventDTO> getEventsByStatus(@PathVariable String status) {
        return eventService.findByStatus(status);
    }
}