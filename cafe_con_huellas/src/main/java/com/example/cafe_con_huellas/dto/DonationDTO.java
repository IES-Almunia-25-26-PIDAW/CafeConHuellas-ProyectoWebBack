package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO para donaciones
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationDTO {

    private Long id;

    // Obligatorio para saber quién hizo la donación
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    // La fecha de la donación no puede ser en el futuro
    @PastOrPresent(message = "La fecha de la donación no puede ser futura")
    private LocalDateTime date;

    // Ejemplo: DINERO, COMIDA, MEDICAMENTOS
    @NotBlank(message = "El tipo de donación es obligatorio")
    @Pattern(regexp = "^(DINERO|RECURSOS|OTROS)$", message = "El tipo debe ser DINERO, RECURSOS u OTROS")
    private String type;

    // Validación crucial para dinero
    @NotNull(message = "El importe de la donación no puede estar vacío")
    @DecimalMin(value = "1.00", message = "La donación mínima permitida es de 1.00 €")
    @DecimalMax(value = "5000.00", message = "Para donaciones mayores a 5,000 € contacte con el refugio")
    @Digits(integer = 4, fraction = 2, message = "Se permiten hasta 4 dígitos y un máximo de 2 decimales (ej: 9999.99)")
    private Double amount;
}
