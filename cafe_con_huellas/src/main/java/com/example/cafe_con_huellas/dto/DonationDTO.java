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

    // Para saber quién hizo la donación
    private Long userId;

    // La fecha de la donación no puede ser en el futuro
    @PastOrPresent(message = "La fecha de la donación no puede ser futura")
    private LocalDateTime date;

    // Ejemplo: MONETARIA, ALIMENTACION, MEDICAMENTOS
    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^(MONETARIA|ALIMENTACION|MATERIAL|JUGUETES|MEDICAMENTOS|SUSCRIPCION|OTROS)$",
            message = "Categoría no válida. Use: MONETARIA, ALIMENTACION, MATERIAL, JUGUETES, MEDICAMENTOS, SUSCRIPCION u OTROS")
    private String category;

    // Ejemplo: EFECTIVO, TRANSFERENCIA, BIZUM
    @NotBlank(message = "El método de donación es obligatorio")
    @Pattern(regexp = "^(EFECTIVO|TRANSFERENCIA|TARJETA|BIZUM|ESPECIE)$",
            message = "Método no válido. Use: EFECTIVO, TRANSFERENCIA, TARJETA, BIZUM o ESPECIE")
    private String method;

    // Validación crucial para dinero
    @NotNull(message = "El importe de la donación no puede estar vacío")
    @DecimalMin(value = "1.00", message = "La donación mínima permitida es de 1.00 €")
    @DecimalMax(value = "5000.00", message = "Para donaciones mayores a 5,000 € contacte con el refugio")
    @Digits(integer = 4, fraction = 2, message = "Se permiten hasta 4 dígitos y un máximo de 2 decimales (ej: 9999.99)")
    private Double amount;

    // Notas adicionales
    @Size(max = 1000, message = "Las notas no pueden superar los 1000 caracteres")
    private String notes;

}
