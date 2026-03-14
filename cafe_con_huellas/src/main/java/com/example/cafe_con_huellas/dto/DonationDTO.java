package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para el registro y consulta de donaciones.
 * <p>
 * Soporta donaciones anónimas (con {@code userId} nulo) y de usuarios registrados.
 * El importe debe estar entre 1,00 € y 5.000,00 €. La categoría y el método
 * se validan contra los valores permitidos de sus respectivos enumerados.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationDTO {

    /** Identificador único de la donación. Nulo en creaciones. */
    private Long id;

    /** Identificador del usuario donante. Nulo si la donación es anónima. */
    private Long userId;

    /** Fecha y hora de la donación. No puede ser futura. */
    @PastOrPresent(message = "La fecha de la donación no puede ser futura")
    private LocalDateTime date;

    /** Categoría de la donación (MONETARIA, ALIMENTACION, MATERIAL, JUGUETES, MEDICAMENTOS, SUSCRIPCION, OTROS). */
    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^(MONETARIA|ALIMENTACION|MATERIAL|JUGUETES|MEDICAMENTOS|SUSCRIPCION|OTROS)$",
            message = "Categoría no válida. Use: MONETARIA, ALIMENTACION, MATERIAL, JUGUETES, MEDICAMENTOS, SUSCRIPCION u OTROS")
    private String category;

    /** Método de pago o entrega (EFECTIVO, TRANSFERENCIA, TARJETA, BIZUM, ESPECIE). */
    @NotBlank(message = "El método de donación es obligatorio")
    @Pattern(regexp = "^(EFECTIVO|TRANSFERENCIA|TARJETA|BIZUM|ESPECIE)$",
            message = "Método no válido. Use: EFECTIVO, TRANSFERENCIA, TARJETA, BIZUM o ESPECIE")
    private String method;

    /** Importe de la donación. Mínimo 1,00 € y máximo 5.000,00 €. */
    @NotNull(message = "El importe de la donación no puede estar vacío")
    @DecimalMin(value = "1.00", message = "La donación mínima permitida es de 1.00 €")
    @DecimalMax(value = "5000.00", message = "Para donaciones mayores a 5,000 € contacte con el refugio")
    @Digits(integer = 4, fraction = 2, message = "Se permiten hasta 4 dígitos y un máximo de 2 decimales (ej: 9999.99)")
    private BigDecimal amount;

    /** Notas adicionales sobre la donación. Máximo 1000 caracteres. */
    @Size(max = 1000, message = "Las notas no pueden superar los 1000 caracteres")
    private String notes;

}
