package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.AdoptionDetailDTO;
import com.example.cafe_con_huellas.model.entity.AdoptionDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Mapper para los detalles legales y técnicos de una adopción.
 * Facilita la comunicación entre la lógica de negocio y la interfaz de usuario.
 */
@Mapper(componentModel = "spring")
public interface AdoptionDetailMapper {

    /* Entity -> DTO
      Extraemos solo el ID de la relación */
    @Mapping(source = "relationship.id", target = "userPetRelationshipId")
    AdoptionDetailDTO toDto(AdoptionDetail entity);

    /* DTO -> Entity
       La relación se asigna desde el service */
    @Mapping(target = "relationship", ignore = true)
    AdoptionDetail toEntity(AdoptionDetailDTO dto);
}
