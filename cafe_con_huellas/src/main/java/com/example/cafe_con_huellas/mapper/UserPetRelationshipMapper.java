package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.UserPetRelationshipDTO;
import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Mapper para la relación (Adopción, Acogida, etc.)
@Mapper(componentModel = "spring")
public interface UserPetRelationshipMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "relationshipType", target = "relationshipType")
    UserPetRelationshipDTO toDto(UserPetRelationship entity);

    /* User y Pet se asignan en el service */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "pet", ignore = true)
    UserPetRelationship toEntity(UserPetRelationshipDTO dto);
}

