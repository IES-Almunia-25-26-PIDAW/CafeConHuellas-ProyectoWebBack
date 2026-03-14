package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link PetVaccine} y {@link PetVaccineDTO}.
 * <p>
 * Al convertir a DTO extrae los IDs de la mascota y la vacuna asociadas.
 * Al convertir a entidad ignora ambas relaciones para evitar que MapStruct
 * intente instanciar objetos incompletos; la integridad referencial
 * se gestiona en el servicio.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PetVaccineMapper {

    /**
     * Convierte una entidad {@link PetVaccine} a su DTO.
     * Extrae los IDs de la mascota y la vacuna asociadas.
     *
     * @param entity entidad a convertir
     * @return {@link PetVaccineDTO} con los datos mapeados
     */
    @Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "vaccine.id", target = "vaccineId")
    PetVaccineDTO toDto(PetVaccine entity);

    /**
     * Convierte un {@link PetVaccineDTO} a su entidad.
     * Los campos {@code pet} y {@code vaccine} se ignoran y se asignan en el servicio.
     *
     * @param dto DTO a convertir
     * @return {@link PetVaccine} con los datos mapeados
     */
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "vaccine", ignore = true)
    PetVaccine toEntity(PetVaccineDTO dto);
}
