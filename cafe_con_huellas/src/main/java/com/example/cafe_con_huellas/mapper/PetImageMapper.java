package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.PetImageDTO;
import com.example.cafe_con_huellas.model.entity.PetImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Mapper para gestionar las imágenes adicionales de las mascotas.
@Mapper(componentModel = "spring")
public interface PetImageMapper {

    @Mapping(source = "pet.id", target = "petId")
    PetImageDTO toDto(PetImage entity);

    // Mascota se asigna en el service mediante su ID para asegurar la consistencia
    @Mapping(target = "pet", ignore = true)
    PetImage toEntity(PetImageDTO dto);
}

