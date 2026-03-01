package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.dto.UserSummaryDTO;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Mapper para la entidad User.
 * Diferencia entre la vista detallada (perfil) y la vista resumida (listados).
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    // De Entidad a DTO Detallado (Perfil)
    @Mapping(source = "role", target = "role")
    UserDetailDTO toDetailDto(User entity);

    // De Entidad a DTO Resumido (Listados administrativos)
    @Mapping(source = "role", target = "role")
    UserSummaryDTO toSummaryDto(User entity);

    // --- MÉTODOS ADICIONALES PARA EL SERVICIO ---

    // Convierte de Detalle a Resumen
    UserSummaryDTO toSummaryDto(UserDetailDTO detailDto);

    // De DTO Detallado a Entidad (Para registro y actualizaciones)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserDetailDTO dto);

    // Helper para el Rol
    default Role roleFromString(String role) {
        if (role == null) return null;
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}
