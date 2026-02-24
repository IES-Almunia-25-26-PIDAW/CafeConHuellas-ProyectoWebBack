package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "El ID del usuario es obligatorio para marcar un favorito")
    private Long userId;

    @NotNull(message = "El ID de la mascota es obligatorio para marcar un favorito")
    private Long petId;
}
