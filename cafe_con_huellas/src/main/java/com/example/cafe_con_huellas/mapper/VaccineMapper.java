package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.VaccineDTO;
import com.example.cafe_con_huellas.model.entity.Vaccine;
import org.mapstruct.Mapper;

// Mapper para las vacunas
@Mapper(componentModel = "spring")
public interface VaccineMapper {

    VaccineDTO toDto(Vaccine entity);

    Vaccine toEntity(VaccineDTO dto);
}
