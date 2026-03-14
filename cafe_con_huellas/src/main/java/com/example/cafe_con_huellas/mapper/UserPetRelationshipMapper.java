package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link UserPetRelationship} y {@link UserPetRelationshipDTO}.
 * <p>
 * Al convertir a DTO extrae los IDs del usuario y la mascota y convierte
 * el tipo de relación de enum a String.
 * Al convertir a entidad ignora el usuario y la mascota, que se asignan en el servicio.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserPetRelationshipMapper {

    /**
     * Convierte una entidad {@link UserPetRelationship} a su DTO.
     * Extrae los IDs del usuario y la mascota y convierte el tipo de relación a String.
     *
     * @param entity entidad a convertir
     * @return {@link UserPetRelationshipDTO} con los datos mapeados
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "relationshipType", target = "relationshipType")
    UserPetRelationshipDTO toDto(UserPetRelationship entity);

    /**
     * Convierte un {@link UserPetRelationshipDTO} a su entidad.
     * Los campos {@code user} y {@code pet} se ignoran y se asignan en el servicio.
     *
     * @param dto DTO a convertir
     * @return {@link UserPetRelationship} con los datos mapeados
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "pet", ignore = true)
    UserPetRelationship toEntity(UserPetRelationshipDTO dto);
}

