package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.DonationDTO;
import com.example.cafe_con_huellas.model.entity.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link Donation} y {@link DonationDTO}.
 * <p>
 * Al convertir a DTO extrae el ID del usuario donante.
 * Al convertir a entidad ignora el usuario y la fecha, que se asignan en el servicio.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface DonationMapper {

    /**
     * Convierte una entidad {@link Donation} a su DTO.
     * Extrae el ID del usuario asociado.
     *
     * @param entity entidad a convertir
     * @return {@link DonationDTO} con los datos mapeados
     */
    @Mapping(source = "user.id", target = "userId")
    DonationDTO toDto(Donation entity);

    /**
     * Convierte un {@link DonationDTO} a su entidad.
     * Los campos {@code user} y {@code date} se ignoran y se asignan en el servicio.
     *
     * @param dto DTO a convertir
     * @return {@link Donation} con los datos mapeados
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "date", ignore = true)
    Donation toEntity(DonationDTO dto);
}
