package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para favoritos de usuarios
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetFavoriteDTO {
    private Long id;
    private Long userId;
    private Long petId;
}
