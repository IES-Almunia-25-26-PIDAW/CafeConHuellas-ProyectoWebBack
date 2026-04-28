package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link AdoptionRequest} y {@link AdoptionRequestDTO}.
 * <p>
 * Al convertir a DTO extrae los datos de contexto del token (ID, nombre de usuario,
 * email y nombre de mascota) para que el administrador los vea directamente.
 * Al convertir a entidad ignora el token, el estado y la fecha de envío,
 * que se gestionan en el servicio.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface AdoptionRequestMapper {

    /**
     * Convierte una entidad {@link AdoptionRequest} a su DTO.
     * Extrae los datos de contexto del token vinculado.
     *
     * @param entity entidad a convertir
     * @return {@link AdoptionRequestDTO} con los datos mapeados
     */
    @Mapping(source = "formToken.id", target = "formTokenId")
    @Mapping(source = "formToken.user.firstName", target = "userName")
    @Mapping(source = "formToken.user.email", target = "userEmail")
    @Mapping(source = "formToken.pet.name", target = "petName")
    @Mapping(source = "relationship.id", target = "relationshipId") // mapea el ID de la relación vinculada
    AdoptionRequestDTO toDto(AdoptionRequest entity);

    /**
     * Convierte un {@link AdoptionRequestDTO} a su entidad.
     * Los campos {@code formToken}, {@code status}, {@code submittedAt}
     * y {@code relationship} se ignoran y se asignan en el servicio.
     *
     * @param dto DTO a convertir
     * @return {@link AdoptionRequest} con los datos mapeados
     */
    @Mapping(target = "formToken", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "relationship", ignore = true) // se asigna en el servicio al aprobar la solicitud
    AdoptionRequest toEntity(AdoptionRequestDTO dto);
}
