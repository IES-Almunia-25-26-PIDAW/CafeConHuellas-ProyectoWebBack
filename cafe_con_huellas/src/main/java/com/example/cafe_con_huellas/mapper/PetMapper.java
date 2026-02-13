package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.dto.PetSummaryDTO;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import com.example.cafe_con_huellas.model.entity.PetImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/*
 * Mapper para la entidad Pet.
 * Gestiona la transformación de la entidad a diferentes proyecciones de DTOs:
 * uno para la vista detallada y otro para listados generales.
 */
@Mapper(componentModel = "spring")
public interface PetMapper {

    // DTO completo de detalle
    @Mapping(source = "category", target = "category")
    @Mapping(source = "images", target = "imageUrls")
    PetDetailDTO toDetailDto(Pet entity);

    package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.PetDetailDTO;
import com.example.cafe_con_huellas.dto.PetSummaryDTO;
import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

    /*
     * Mapper para la entidad Pet.
     * Gestiona la transformación de la entidad a diferentes proyecciones de DTOs:
     * uno para la vista detallada y otro para listados generales.
     */
    @Mapper(componentModel = "spring")
    public interface PetMapper {

        // DTO completo de detalle
        @Mapping(source = "category", target = "category")
        @Mapping(source = "images", target = "imageUrls")
        PetDetailDTO toDetailDto(Pet entity);

        // Convierte DTO a entidad para crear/actualizar
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
            pet.setImageUrl(dto.getImageUrl());
            return pet;
        }


        // DTO para listados
        @Mapping(source = "category", target = "category")
        PetSummaryDTO toSummaryDto(Pet entity);

        // Convierte lista de imágenes en lista de URLs
        default List<String> mapImagesToUrls(List<PetImage> images) {
            if (images == null) return List.of();
            return images.stream()
                    .map(PetImage::getImageUrl)
                    .toList();
        }
    }


    // DTO para listados
    @Mapping(source = "category", target = "category")
    PetSummaryDTO toSummaryDto(Pet entity);

    // Convierte lista de imágenes en lista de URLs
    default List<String> mapImagesToUrls(List<PetImage> images) {
        if (images == null) return List.of();
        return images.stream()
                .map(PetImage::getImageUrl)
                .toList();
    }

    // Convierte lista de PetImage a lista de URLs
    default List<String> mapImagesToUrls(List<PetImage> images) {
        if (images == null) return List.of();
        return images.stream()
                .map(PetImage::getImageUrl)
                .toList();
    }

    // Convierte String a PetCategory
    default PetCategory categoryFromString(String category) {
        if (category == null) return null;
        try {
            return PetCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Categoría inválida: " + category);
        }
    }


}