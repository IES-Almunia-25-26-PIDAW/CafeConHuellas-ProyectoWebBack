package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct para la conversión entre {@link Vaccine} y {@link VaccineDTO}.
 * <p>
 * Conversión directa sin mapeos especiales ya que todos los campos
 * tienen el mismo nombre en la entidad y el DTO.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface VaccineMapper {

    /**
     * Convierte una entidad {@link Vaccine} a su DTO.
     *
     * @param entity entidad a convertir
     * @return {@link VaccineDTO} con los datos mapeados
     */
    VaccineDTO toDto(Vaccine entity);

    /**
     * Convierte un {@link VaccineDTO} a su entidad.
     *
     * @param dto DTO a convertir
     * @return {@link Vaccine} con los datos mapeados
     */
    Vaccine toEntity(VaccineDTO dto);
}
