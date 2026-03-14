package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.model.entity.PetImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link PetImage} y {@link PetImageDTO}.
 * <p>
 * Al convertir a DTO extrae el ID de la mascota asociada.
 * Al convertir a entidad ignora la mascota, que se asigna en el servicio
 * para garantizar la consistencia referencial.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PetImageMapper {

    /**
     * Convierte una entidad {@link PetImage} a su DTO.
     * Extrae el ID de la mascota a la que pertenece la imagen.
     *
     * @param entity entidad a convertir
     * @return {@link PetImageDTO} con los datos mapeados
     */
    @Mapping(source = "pet.id", target = "petId")
    PetImageDTO toDto(PetImage entity);

    /**
     * Convierte un {@link PetImageDTO} a su entidad.
     * El campo {@code pet} se ignora y se asigna en el servicio.
     *
     * @param dto DTO a convertir
     * @return {@link PetImage} con los datos mapeados
     */
    @Mapping(target = "pet", ignore = true)
    PetImage toEntity(PetImageDTO dto);
}

