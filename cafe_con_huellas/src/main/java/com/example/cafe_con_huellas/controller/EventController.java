package com.example.cafe_con_huellas.controller;

import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controlador REST para la gestión de eventos del refugio.
 * Expone los endpoints necesarios para crear, consultar y filtrar eventos.
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Obtiene el listado completo de todos los eventos (pasados y futuros)
    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.findAll();
    }

    // Busca un evento específico por su ID único
    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    // Registra un nuevo evento en el sistema
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO createEvent(@Valid @RequestBody EventDTO eventDTO) {
        return eventService.save(eventDTO);
    }

    // Actualiza un evento existente (usamos el mismo método save del service)
    @PutMapping("/{id}")
    public EventDTO updateEvent(@PathVariable Long id, @Valid @RequestBody EventDTO eventDTO) {
        eventDTO.setId(id); // Nos aseguramos de actualizar el ID correcto
        return eventService.save(eventDTO);
    }

    // Elimina un evento de la base de datos
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteById(id);
    }

    // --- Endpoints de Filtrado ---

    // Obtiene solo los próximos eventos
    @GetMapping("/upcoming")
    public List<EventDTO> getUpcomingEvents() {
        return eventService.findUpcomingEvents();
    }

    // Filtra eventos por estado (ej: /api/events/status/PROGRAMADO)
    @GetMapping("/status/{status}")
    public List<EventDTO> getEventsByStatus(@PathVariable String status) {
        return eventService.findByStatus(status);
    }
}