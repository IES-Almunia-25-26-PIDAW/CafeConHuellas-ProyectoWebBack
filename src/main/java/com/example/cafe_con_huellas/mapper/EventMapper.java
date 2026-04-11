package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.EventDTO;
import com.example.cafe_con_huellas.model.entity.Event;
import com.example.cafe_con_huellas.model.entity.EventStatus;
import com.example.cafe_con_huellas.model.entity.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link Event} y {@link EventDTO}.
 * <p>
 * MapStruct convierte automáticamente los enums {@link EventType} y {@link EventStatus}
 * a String y viceversa. La fecha de creación se ignora al convertir a entidad
 * ya que es gestionada automáticamente por el sistema.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    /**
     * Convierte una entidad {@link Event} a su DTO.
     * Los enums se convierten automáticamente a String.
     *
     * @param entity entidad a convertir
     * @return {@link EventDTO} con los datos mapeados
     */
    EventDTO toDto(Event entity);

    /**
     * Convierte un {@link EventDTO} a su entidad.
     * El campo {@code createdAt} se ignora ya que lo gestiona el sistema.
     *
     * @param dto DTO a convertir
     * @return {@link Event} con los datos mapeados
     */
    @Mapping(target = "createdAt", ignore = true)
    Event toEntity(EventDTO dto);

}
