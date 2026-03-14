package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.dto.UserSummaryDTO;
import com.example.cafe_con_huellas.model.entity.Role;
import com.example.cafe_con_huellas.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión entre {@link User} y sus proyecciones DTO.
 * <p>
 * Diferencia entre la vista detallada para el perfil ({@link UserDetailDTO})
 * y la vista resumida para listados administrativos ({@link UserSummaryDTO}).
 * La contraseña se ignora siempre al convertir a entidad para no sobrescribirla.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convierte una entidad {@link User} a su DTO detallado.
     * El rol se convierte automáticamente de enum a String.
     *
     * @param entity entidad a convertir
     * @return {@link UserDetailDTO} con el perfil completo del usuario
     */
    @Mapping(source = "role", target = "role")
    UserDetailDTO toDetailDto(User entity);


    /**
     * Convierte una entidad {@link User} a su DTO resumido para listados.
     *
     * @param entity entidad a convertir
     * @return {@link UserSummaryDTO} con los datos esenciales del usuario
     */
    @Mapping(source = "role", target = "role")
    UserSummaryDTO toSummaryDto(User entity);

    // --- MÉTODOS ADICIONALES PARA EL SERVICIO ---

    /**
     * Convierte un {@link UserDetailDTO} a su versión resumida.
     * Útil para construir listados a partir de datos ya cargados.
     *
     * @param detailDto DTO detallado a convertir
     * @return {@link UserSummaryDTO} con los datos esenciales
     */
    UserSummaryDTO toSummaryDto(UserDetailDTO detailDto);

    /**
     * Convierte un {@link UserDetailDTO} a su entidad.
     * La contraseña se ignora para no sobrescribir el hash BCrypt almacenado.
     *
     * @param dto DTO a convertir
     * @return {@link User} con los datos mapeados sin contraseña
     */
    @Mapping(target = "password", ignore = true)
    User toEntity(UserDetailDTO dto);

    /**
     * Helper para el role.
     * Convierte un String al enum {@link Role} correspondiente.
     * Si el valor es nulo o no válido, devuelve {@code null} o {@link Role#USER} respectivamente.
     *
     * @param role nombre del rol en texto
     * @return {@link Role} correspondiente, o {@link Role#USER} si no es reconocido
     */
    default Role roleFromString(String role) {
        if (role == null) return null;
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}
