package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdoptionRequestMapper {

    // Entity -> DTO: extraemos los datos de contexto del token
    @Mapping(source = "formToken.id", target = "formTokenId")
    @Mapping(source = "formToken.user.firstName", target = "userName")
    @Mapping(source = "formToken.user.email", target = "userEmail")
    @Mapping(source = "formToken.pet.name", target = "petName")
    AdoptionRequestDTO toDto(AdoptionRequest entity);

    // DTO -> Entity: el formToken se asigna desde el service
    @Mapping(target = "formToken", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    AdoptionRequest toEntity(AdoptionRequestDTO dto);
}
