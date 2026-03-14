package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro y consulta de mascotas favoritas de un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetFavoriteDTO {

    /** Identificador único del registro de favorito. Nulo en creaciones. */
    private Long id;

    /** Identificador del usuario que marca la mascota como favorita. */
    @NotNull(message = "El ID del usuario es obligatorio para marcar un favorito")
    private Long userId;

    /** Identificador de la mascota marcada como favorita. */
    @NotNull(message = "El ID de la mascota es obligatorio para marcar un favorito")
    private Long petId;
}
