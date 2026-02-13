package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.UserPetFavoriteDTO;
import com.example.cafe_con_huellas.model.entity.UserPetFavorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Mapper para la gestión de favoritos de mascotas.
@Mapper(componentModel = "spring")
public interface UserPetFavoriteMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pet.id", target = "petId")
    UserPetFavoriteDTO toDto(UserPetFavorite entity);

    /* User y Pet se setean en el service */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "pet", ignore = true)
    UserPetFavorite toEntity(UserPetFavoriteDTO dto);
}
