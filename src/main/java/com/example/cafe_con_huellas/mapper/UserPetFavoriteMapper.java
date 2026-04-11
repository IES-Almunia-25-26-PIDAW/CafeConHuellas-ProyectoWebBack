package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.UserPetFavoriteDTO;
import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link UserPetFavorite} y {@link UserPetFavoriteDTO}.
 * <p>
 * Al convertir a DTO extrae los IDs del usuario y la mascota.
 * Al convertir a entidad ignora ambas relaciones, que se asignan en el servicio.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserPetFavoriteMapper {

    /**
     * Convierte una entidad {@link UserPetFavorite} a su DTO.
     * Extrae los IDs del usuario y la mascota asociados.
     *
     * @param entity entidad a convertir
     * @return {@link UserPetFavoriteDTO} con los datos mapeados
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pet.id", target = "petId")
    UserPetFavoriteDTO toDto(UserPetFavorite entity);

    /**
     * Convierte un {@link UserPetFavoriteDTO} a su entidad.
     * Los campos {@code user} y {@code pet} se ignoran y se asignan en el servicio.
     *
     * @param dto DTO a convertir
     * @return {@link UserPetFavorite} con los datos mapeados
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "pet", ignore = true)
    UserPetFavorite toEntity(UserPetFavoriteDTO dto);
}
