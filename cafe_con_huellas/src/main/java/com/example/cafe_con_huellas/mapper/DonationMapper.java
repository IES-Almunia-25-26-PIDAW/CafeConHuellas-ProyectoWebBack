package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.DonationDTO;
import com.example.cafe_con_huellas.model.entity.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Mapper para la gestión de donaciones.
 * Transforma la entidad Donation en un DTO simplificado para transacciones.
 */
@Mapper(componentModel = "spring")
public interface DonationMapper {

    @Mapping(source = "user.id", target = "userId")
    DonationDTO toDto(Donation entity);

    // El usuario se setea en el service
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "date", ignore = true)
    Donation toEntity(DonationDTO dto);
}
