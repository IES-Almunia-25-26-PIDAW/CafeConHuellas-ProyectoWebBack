package com.example.cafe_con_huellas.mapper;

import com.example.cafe_con_huellas.dto.UserDetailDTO;
import com.example.cafe_con_huellas.dto.UserSummaryDTO;
import com.example.cafe_con_huellas.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Mapper para la entidad User.
 * Diferencia entre la vista detallada (perfil) y la vista resumida (listados).
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "role")
    UserDetailDTO toDetailDto(User entity);

    @Mapping(source = "role", target = "role")
    UserSummaryDTO toSummaryDto(User entity);
}
