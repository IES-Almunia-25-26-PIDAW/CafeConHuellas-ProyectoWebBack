package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Mapper para el historial de vacunación.
@Mapper(componentModel = "spring")
public interface PetVaccineMapper {

    @Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "vaccine.id", target = "vaccineId")
    PetVaccineDTO toDto(PetVaccine entity);

    // Pet y Vaccine se asignan desde el service
    /* Se ignoran Pet y Vaccine para evitar que MapStruct intente instanciar
     * objetos incompletos; la integridad referencial se gestiona en la capa de servicio.
     */
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "vaccine", ignore = true)
    PetVaccine toEntity(PetVaccineDTO dto);
}
