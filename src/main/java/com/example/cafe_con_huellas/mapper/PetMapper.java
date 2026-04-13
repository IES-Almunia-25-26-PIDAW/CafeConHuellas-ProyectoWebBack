package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.dto.PetSummaryDTO;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import com.example.cafe_con_huellas.model.entity.PetImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct para la conversión entre {@link Pet} y sus distintas proyecciones DTO.
 * <p>
 * Ofrece dos vistas: {@link PetDetailDTO} para la ficha completa del animal
 * y {@link PetSummaryDTO} para los listados del catálogo.
 * Incluye métodos helper para convertir la categoría desde String y
 * mapear la galería de imágenes a una lista de URLs.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PetMapper {

    /**
     * Convierte una entidad {@link Pet} a su DTO detallado.
     * Mapea la galería de imágenes a una lista de URLs.
     *
     * @param entity entidad a convertir
     * @return {@link PetDetailDTO} con todos los datos de la mascota
     */
    @Mapping(source = "category", target = "category")
    @Mapping(source = "images", target = "imageUrls")
    PetDetailDTO toDetailDto(Pet entity);

    /**
     * Convierte un {@link PetDetailDTO} a su entidad.
     * Las imágenes de la galería no se mapean; se gestionan de forma independiente.
     *
     * @param dto DTO a convertir
     * @return {@link Pet} con los datos básicos mapeados
     */
    default Pet toEntity(PetDetailDTO dto) {
        if (dto == null) return null;

        Pet pet = new Pet();
        pet.setId(dto.getId());
        pet.setName(dto.getName());
        pet.setDescription(dto.getDescription());
        pet.setBreed(dto.getBreed());
        pet.setCategory(categoryFromString(dto.getCategory()));
        pet.setAge(dto.getAge());
        pet.setWeight(dto.getWeight());
        pet.setNeutered(dto.getNeutered());
        pet.setIsPpp(dto.getIsPpp());
        pet.setUrgentAdoption(dto.getUrgentAdoption());
        pet.setImageUrl(dto.getImageUrl());
        return pet;
    }


    /**
     * Convierte una entidad {@link Pet} a su DTO resumido para listados.
     *
     * @param entity entidad a convertir
     * @return {@link PetSummaryDTO} con los datos esenciales de la mascota
     */
    @Mapping(source = "category", target = "category")
    PetSummaryDTO toSummaryDto(Pet entity);

    /**
     * Convierte una lista de entidades {@link PetImage} en una lista de URLs.
     *
     * @param images lista de imágenes de la galería
     * @return lista de URLs de las imágenes, o lista vacía si es nula
     */
    default List<String> mapImagesToUrls(List<PetImage> images) {
        if (images == null) return List.of();
        return images.stream()
                .map(PetImage::getImageUrl)
                .toList();
    }


    /**
     * Convierte un String a su valor enum {@link PetCategory} correspondiente.
     * La comparación es insensible a mayúsculas.
     *
     * @param category nombre de la categoría en texto
     * @return {@link PetCategory} correspondiente
     * @throws RuntimeException si el valor no corresponde a ninguna categoría válida
     */
    default PetCategory categoryFromString(String category) {
        if (category == null) return null;
        try {
            return PetCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Categoría inválida: " + category);
        }
    }

    /**
     * Convierte un {@link PetDetailDTO} a su versión resumida.
     * Útil para los listados del catálogo a partir de datos ya cargados.
     *
     * @param detailDto DTO detallado a convertir
     * @return {@link PetSummaryDTO} con los datos esenciales
     */
    PetSummaryDTO toSummaryDto(PetDetailDTO detailDto);


}