package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link AdoptionDetail} y {@link AdoptionDetailDTO}.
 * <p>
 * Al convertir a DTO extrae el ID de la relación usuario-mascota.
 * Al convertir a entidad ignora la relación, que se asigna manualmente en el servicio.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface AdoptionDetailMapper {

    /**
     * Convierte una entidad {@link AdoptionDetail} a su DTO.
     * Extrae el ID de la relación asociada.
     *
     * @param entity entidad a convertir
     * @return {@link AdoptionDetailDTO} con los datos mapeados
     */
    @Mapping(source = "relationship.id", target = "userPetRelationshipId")
    AdoptionDetailDTO toDto(AdoptionDetail entity);

    /**
     * Convierte un {@link AdoptionDetailDTO} a su entidad.
     * La relación {@code relationship} se ignora y se asigna en el servicio.
     *
     * @param dto DTO a convertir
     * @return {@link AdoptionDetail} con los datos mapeados
     */
    @Mapping(target = "relationship", ignore = true)
    AdoptionDetail toEntity(AdoptionDetailDTO dto);
}
