package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.model.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Mapper para la gestión de eventos del refugio.
 * Realiza la conversión entre la entidad Event y su DTO correspondiente.
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    // Convierte la entidad de la base de datos a un DTO para el Frontend
    // MapStruct se encarga de convertir los Enums a String automáticamente
    EventDTO toDto(Event entity);

    // Convierte el DTO recibido en una entidad para persistir en la base de datos
    // Ignoramos 'createdAt' porque es un campo gestionado por el sistema/BD
    @Mapping(target = "createdAt", ignore = true)
    Event toEntity(EventDTO dto);

}
