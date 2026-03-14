package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para la transferencia de datos de eventos del refugio.
 * <p>
 * Valida que la fecha del evento sea futura, que el tipo y estado
 * coincidan con los valores permitidos de sus enumerados, y que
 * la capacidad máxima no supere las 10.000 personas.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {

    /** Identificador único del evento. Nulo en creaciones. */
    private Long id;

    /** Nombre del evento. Entre 5 y 100 caracteres. */
    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre debe tener entre 5 y 100 caracteres")
    private String name;

    /** Descripción detallada del evento. Mínimo 20 caracteres. */
    @NotBlank(message = "La descripción del evento es obligatoria")
    @Size(min = 20, message = "La descripción debe ser detallada (mínimo 20 caracteres)")
    private String description;

    /** Fecha y hora programada para el evento. Debe ser una fecha futura. */
    @NotNull(message = "La fecha del evento es obligatoria")
    @Future(message = "La fecha del evento debe ser una fecha futura")
    private LocalDateTime eventDate;

    /** Dirección o lugar físico donde se celebrará el evento. */
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres")
    private String location;

    /** URL de la imagen promocional del evento. Debe ser una URL http/https válida. Opcional. */
    @Pattern(regexp = "^https?://.*", message = "La imagen debe ser una URL válida (http/https)")
    private String imageUrl;

    /** Tipo de evento (ADOPCION, MERCADILLO, EDUCACION, RECAUDACION, OTRO). */
    @NotBlank(message = "El tipo de evento es obligatorio")
    @Pattern(regexp = "^(ADOPCION|MERCADILLO|EDUCACION|RECAUDACION|OTRO)$",
            message = "Tipo de evento no válido. Use: ADOPCION, MERCADILLO, EDUCACION, RECAUDACION u OTRO")
    private String eventType;

    /** Estado actual del evento (PROGRAMADO, EN_CURSO, FINALIZADO, CANCELADO). */
    @NotBlank(message = "El estado del evento es obligatorio")
    @Pattern(regexp = "^(PROGRAMADO|EN_CURSO|FINALIZADO|CANCELADO)$",
            message = "Estado no válido. Use: PROGRAMADO, EN_CURSO, FINALIZADO o CANCELADO")
    private String status;

    /** Aforo máximo de asistentes. Positivo y no superior a 10.000. Opcional. */
    @Positive(message = "La capacidad máxima debe ser un número positivo")
    @Max(value = 10000, message = "Para eventos de más de 10.000 personas, contacte con soporte")
    private Integer maxCapacity;

    /** Fecha y hora de creación del registro. Solo lectura. */
    private LocalDateTime createdAt;

}
